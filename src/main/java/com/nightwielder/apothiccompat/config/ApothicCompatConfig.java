package com.nightwielder.apothiccompat.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.nightwielder.apothiccompat.ApothicCompat;
import dev.shadowsoffire.apotheosis.adventure.AdventureConfig;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.core.registries.Registries;
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
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ApothicCompatConfig {
    private static final String FILE_NAME = "apothic_compat.toml";
    private static final String IMC_METHOD = "loot_category_override";

    private static final String DEFAULT_CONTENTS = """
            # Apothic Compat: user-defined loot category overrides.
            #
            # Maps Minecraft items (or item tags) to Apotheosis loot categories so the
            # affinity system can roll the right gem/affix pools on modded gear that
            # Apotheosis does not categorize on its own. Entries here are sent to
            # Apotheosis via IMC at startup, alongside the mod's built-in compat
            # modules. Edit this file then run /apothiccompat reload (op 2) to apply
            # changes without restarting the server.
            #
            # Built-in loot category names (Apotheosis 7.4.8):
            #   sword, heavy_weapon, bow, crossbow, shield,
            #   helmet, chestplate, leggings, boots, pickaxe, shovel, none
            #
            # Other mods can register additional categories at startup. Any name
            # that resolves at apply time is accepted; unknown names are skipped
            # with a warning. Known third-party categories from Fallen Gems &
            # Affixes:
            #   staffs           (requires Iron's Spellbooks)
            #   celestial_melee  (requires Celestisynth)
            #   celestial_ranged (requires Celestisynth)
            #
            # Note: items already listed in Apotheosis's own adventure.cfg under
            # Equipment Type Overrides take precedence over this config. To override
            # those, edit adventure.cfg directly.
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

    private static FileTime lastAppliedMTime;

    private ApothicCompatConfig() {}

    /** Initial application during InterModEnqueueEvent. Uses IMC, the only path that works pre-game. */
    public static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        lastAppliedMTime = readMTime(path);
        process((item, categoryName) ->
                InterModComms.sendTo("apotheosis", IMC_METHOD, () -> Map.entry(item, categoryName)));
    }

    /**
     * Runtime reapplication for /apothiccompat reload. IMC is dead after mod loading, so we write
     * directly to Apotheosis's live override map. We also mirror into AdventureModule.IMC_TYPE_OVERRIDES
     * (via reflection) because AdventureConfig.load clears TYPE_OVERRIDES and re-copies from there on
     * any subsequent Apotheosis config reload; without the mirror, our entries would vanish.
     *
     * Skips the whole apply cycle if the file's mtime hasn't advanced since the previous apply.
     *
     * Note: this is purely additive. Removing an entry from the .toml and reloading does NOT remove the
     * existing override (matches IMC re-send semantics). Restart the server to drop entries.
     */
    public static ReloadResult reload() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        FileTime current = readMTime(path);
        if (current != null && current.equals(lastAppliedMTime)) {
            return ReloadResult.ofUnchanged();
        }
        Map<ResourceLocation, LootCategory> imcMirror = getImcOverrideMap();
        int count = process((item, categoryName) -> {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            LootCategory cat = LootCategory.byId(categoryName);
            if (id == null || cat == null) return;
            AdventureConfig.TYPE_OVERRIDES.put(id, cat);
            if (imcMirror != null) imcMirror.put(id, cat);
        });
        if (current != null) lastAppliedMTime = current;
        return ReloadResult.ofApplied(count);
    }

    private static FileTime readMTime(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException e) {
            ApothicCompat.LOGGER.warn("Failed to stat {}", FILE_NAME, e);
            return null;
        }
    }

    public record ReloadResult(boolean unchanged, int count) {
        public static ReloadResult ofUnchanged() { return new ReloadResult(true, 0); }
        public static ReloadResult ofApplied(int count) { return new ReloadResult(false, count); }
    }

    /** Reads the file and dispatches each valid (item, category) pair to {@code action}. Returns the count applied. */
    private static int process(BiConsumer<Item, String> action) {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        int[] count = {0};
        try (CommentedFileConfig config = CommentedFileConfig.builder(path).sync().build()) {
            try {
                config.load();
            } catch (ParsingException e) {
                // NightConfig throws this when the file ends without a trailing newline after a
                // table header like [tag_overrides]. By the time it throws, every entry above the
                // EOF has already been parsed into the config object, so we can safely keep going.
                if (e.getMessage() != null && e.getMessage().contains("Not enough data available")) {
                    ApothicCompat.LOGGER.debug("Tolerating trailing-EOF parse hiccup in {}: {}", FILE_NAME, e.getMessage());
                } else {
                    throw e;
                }
            }
            count[0] += processItemOverrides(config, action);
            count[0] += processTagOverrides(config, action);
        } catch (Exception e) {
            ApothicCompat.LOGGER.error("Failed to read {}", FILE_NAME, e);
        }
        return count[0];
    }

    private static void ensureDefaultFile(Path path) {
        if (Files.exists(path)) return;
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, DEFAULT_CONTENTS);
        } catch (IOException e) {
            ApothicCompat.LOGGER.error("Failed to create default {}", FILE_NAME, e);
        }
    }

    private static int processItemOverrides(CommentedFileConfig config, BiConsumer<Item, String> action) {
        Object raw = config.get("item_overrides");
        if (!(raw instanceof UnmodifiableConfig section)) return 0;
        int count = 0;
        for (UnmodifiableConfig.Entry entry : section.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!(value instanceof String categoryName)) {
                ApothicCompat.LOGGER.warn("[item_overrides] '{}' must map to a string category, got {}", key, value);
                continue;
            }
            if (LootCategory.byId(categoryName) == null) {
                ApothicCompat.LOGGER.warn("[item_overrides] unknown loot category '{}', skipping override for '{}'. Either misspelled or registered by a mod that isn't installed.", categoryName, key);
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
        if (!(raw instanceof UnmodifiableConfig section)) return 0;
        int count = 0;
        for (UnmodifiableConfig.Entry entry : section.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!(value instanceof String categoryName)) {
                ApothicCompat.LOGGER.warn("[tag_overrides] '{}' must map to a string category, got {}", key, value);
                continue;
            }
            if (LootCategory.byId(categoryName) == null) {
                ApothicCompat.LOGGER.warn("[tag_overrides] unknown loot category '{}', skipping override for '{}'. Either misspelled or registered by a mod that isn't installed.", categoryName, key);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id == null) {
                ApothicCompat.LOGGER.warn("[tag_overrides] invalid tag id '{}'", key);
                continue;
            }
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, id);
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
