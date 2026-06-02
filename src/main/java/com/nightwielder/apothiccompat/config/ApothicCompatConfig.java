package com.nightwielder.apothiccompat.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.nightwielder.apothiccompat.ApothicCompat;
import com.nightwielder.apothiccompat.util.CompatImc;
import shadows.apotheosis.adventure.AdventureConfig;
import shadows.apotheosis.adventure.AdventureModule;
import shadows.apotheosis.adventure.loot.LootCategory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ApothicCompatConfig {
    private static final String FILE_NAME = "apothic_compat.toml";
    private static final String IMC_METHOD = "loot_category_override";

    private static final String DEFAULT_CONTENTS = """
            # User-defined loot category overrides for Apothic Compat.
            #
            # Maps Minecraft items (or item tags) to Apotheosis loot categories so the
            # affinity system can roll the right gem/affix pools on modded gear that
            # Apotheosis does not categorize on its own. Entries here are sent to
            # Apotheosis via IMC at startup, alongside the mod's built-in compat
            # modules. Edit this file then run /apothiccompat reload (op 2) to apply
            # changes without restarting the server.
            #
            # Valid loot category names (Apotheosis 6.5.2):
            #   sword, heavy_weapon, trident, bow, crossbow, shield,
            #   helmet, chestplate, leggings, boots, pickaxe, shovel, none
            #
            # Keys MUST be quoted because item and tag IDs contain a ':' separator
            # (namespace:path), which TOML does not allow in bare keys.

            # ----------------------------------------------------------------------
            # Per-item overrides.
            #   key   = full item id (namespace:path)
            #   value = loot category name from the list above
            #
            # Example:
            #   "ruins:greatsword" = "heavy_weapon"
            #   "simplyswords:greathammer" = "heavy_weapon"
            # ----------------------------------------------------------------------
            [item_overrides]

            # ----------------------------------------------------------------------
            # Per-tag overrides. Every item carrying the tag receives the category.
            #   key   = full tag id (namespace:path)
            #   value = loot category name from the list above
            #
            # Tags are resolved at apply time, so datapack-only tags that load with a
            # world may not be visible during early startup. /apothiccompat reload
            # runs after world load, so tag expansion there sees datapack tags.
            #
            # Example:
            #   "simplyswords:greathammers" = "heavy_weapon"
            # ----------------------------------------------------------------------
            [tag_overrides]
            """;

    private ApothicCompatConfig() {}

    // first apply during InterModEnqueueEvent, over IMC since that's the only path that works pre-game
    public static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        process((item, categoryName) ->
                InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, categoryName)));
    }

    // reapply for /apothiccompat reload. IMC is dead after load so write straight to Apoth's live
    // override map and mirror into AdventureModule.IMC_TYPE_OVERRIDES by reflection, otherwise the
    // entries vanish when AdventureConfig.load recopies on the next Apoth reload. always re-reads the
    // toml since the command is operator-invoked. additive only, so dropping a toml entry then reloading
    // keeps the live override and a restart is needed to drop it.
    public static ReloadResult reload() {
        Map<ResourceLocation, LootCategory> imcMirror = getImcOverrideMap();
        int count = process((item, categoryName) -> {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            LootCategory cat = LootCategory.byId(categoryName);
            if (id == null || cat == null) {
                return;
            }
            AdventureConfig.TYPE_OVERRIDES.put(id, cat);
            if (imcMirror != null) {
                imcMirror.put(id, cat);
            }
        });
        ApothicCompat.LOGGER.info("Apothic Compat config reloaded.");
        return ReloadResult.ofApplied(count);
    }

    public record ReloadResult(int count) {
        public static ReloadResult ofApplied(int count) { return new ReloadResult(count); }
    }

    // Second-pass re-categorization after deferred mod init (FMLLoadCompleteEvent). The IMC window is closed,
    // so swap CompatImc's sink to write Apotheosis's live override map and mirror IMC_TYPE_OVERRIDES the same
    // way reload() does, run the supplied module dispatch, then restore the IMC sink. Only items whose
    // category changed from the first-pass IMC value are written; returns that change count.
    public static int reapply(Runnable dispatch) {
        Map<ResourceLocation, LootCategory> imcMirror = getImcOverrideMap();
        int[] changed = {0};
        CompatImc.setSink((item, categoryName) -> {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            LootCategory cat = LootCategory.byId(categoryName);
            if (id == null || cat == null) {
                return;
            }
            LootCategory prev = imcMirror != null ? imcMirror.get(id) : AdventureConfig.TYPE_OVERRIDES.get(id);
            if (cat.equals(prev)) {
                return;
            }
            AdventureConfig.TYPE_OVERRIDES.put(id, cat);
            if (imcMirror != null) {
                imcMirror.put(id, cat);
            }
            changed[0]++;
        });
        try {
            dispatch.run();
        } finally {
            CompatImc.resetSink();
        }
        return changed[0];
    }

    // reads the file and sends each valid (item, category) pair to action, returning the applied count
    private static int process(BiConsumer<Item, String> action) {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        int[] count = {0};
        try (CommentedFileConfig config = CommentedFileConfig.builder(path).sync().build()) {
            loadTolerant(config);
            count[0] += processItemOverrides(config, action);
            count[0] += processTagOverrides(config, action);
        } catch (Exception e) {
            ApothicCompat.LOGGER.error("Failed to read {}", FILE_NAME, e);
        }
        return count[0];
    }

    private static void loadTolerant(CommentedFileConfig config) {
        try {
            config.load();
        } catch (ParsingException e) {
            // NightConfig throws this when the file ends without a trailing newline after a table header
            // like [tag_overrides]. everything above the EOF is already parsed so keep going.
            if (e.getMessage() != null && e.getMessage().contains("Not enough data available")) {
                ApothicCompat.LOGGER.debug("Tolerating trailing-EOF parse hiccup in {}: {}", FILE_NAME, e.getMessage());
            } else {
                throw e;
            }
        }
    }

    private static void ensureDefaultFile(Path path) {
        if (Files.exists(path)) {
            return;
        }
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, DEFAULT_CONTENTS);
        } catch (IOException e) {
            ApothicCompat.LOGGER.error("Failed to create default {}", FILE_NAME, e);
        }
    }

    private static int processItemOverrides(CommentedFileConfig config, BiConsumer<Item, String> action) {
        Object raw = config.get("item_overrides");
        if (!(raw instanceof UnmodifiableConfig section)) {
            return 0;
        }
        int count = 0;
        for (UnmodifiableConfig.Entry entry : section.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!(value instanceof String categoryName)) {
                ApothicCompat.LOGGER.warn("[item_overrides] '{}' must map to a string category, got {}", key, value);
                continue;
            }
            if (LootCategory.byId(categoryName) == null) {
                ApothicCompat.LOGGER.warn("[item_overrides] '{}' uses unknown category '{}'", key, categoryName);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id == null) {
                ApothicCompat.LOGGER.warn("[item_overrides] invalid item id '{}'", key);
                continue;
            }
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) {
                ApothicCompat.LOGGER.info("[item_overrides] item '{}' not present; skipping", key);
                continue;
            }
            action.accept(item, categoryName);
            count++;
        }
        return count;
    }

    private static int processTagOverrides(CommentedFileConfig config, BiConsumer<Item, String> action) {
        Object raw = config.get("tag_overrides");
        if (!(raw instanceof UnmodifiableConfig section)) {
            return 0;
        }
        int count = 0;
        for (UnmodifiableConfig.Entry entry : section.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!(value instanceof String categoryName)) {
                ApothicCompat.LOGGER.warn("[tag_overrides] '{}' must map to a string category, got {}", key, value);
                continue;
            }
            if (LootCategory.byId(categoryName) == null) {
                ApothicCompat.LOGGER.warn("[tag_overrides] '{}' uses unknown category '{}'", key, categoryName);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id == null) {
                ApothicCompat.LOGGER.warn("[tag_overrides] invalid tag id '{}'", key);
                continue;
            }
            TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, id);
            ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
            if (tag.isEmpty()) {
                ApothicCompat.LOGGER.info("[tag_overrides] tag '{}' empty or not yet bound; skipping", key);
                continue;
            }
            for (Item item : tag) {
                action.accept(item, categoryName);
                count++;
            }
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    private static Map<ResourceLocation, LootCategory> getImcOverrideMap() {
        try {
            Field field = AdventureModule.class.getDeclaredField("IMC_TYPE_OVERRIDES");
            field.setAccessible(true);
            return (Map<ResourceLocation, LootCategory>) field.get(null);
        } catch (ReflectiveOperationException e) {
            ApothicCompat.LOGGER.warn("Could not access AdventureModule.IMC_TYPE_OVERRIDES; reload will not persist across Apotheosis config reloads", e);
            return null;
        }
    }
}
