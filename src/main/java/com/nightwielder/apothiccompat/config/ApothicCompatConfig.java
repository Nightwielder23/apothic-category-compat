package com.nightwielder.apothiccompat.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.nightwielder.apothiccompat.ApothicCompat;
import com.nightwielder.apothiccompat.compat.AffixBlacklist;
import com.nightwielder.apothiccompat.compat.RegistryLookup;
import com.nightwielder.apothiccompat.util.CompatImc;
import dev.shadowsoffire.apotheosis.adventure.AdventureConfig;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public final class ApothicCompatConfig {
    private static final String FILE_NAME = "apothic_compat.toml";

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
            #   sword, heavy_weapon, trident, bow, crossbow, shield,
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
            # Affix blacklist. Stops the listed affixes from rolling on newly
            # generated gear (loot drops, reforging, trades, gem application).
            # Existing items keep any affixes they already have; this only blocks
            # future rolls. Apotheosis's own datapack affix overrides still win.
            #
            #   value = array of affix ids, each "namespace:path"
            #           e.g. "apotheosis:berserking", "apotheosis:telepathic"
            #
            # Find affix ids from JEI tooltips on affixed gear, or from the files
            # under data/<namespace>/affixes/ inside a mod's jar. Edit this list then
            # run /apothiccompat reload (op 2) to re-apply without a restart.
            #
            # Example:
            #   affix_blacklist = ["apotheosis:berserking", "apotheosis:telepathic"]
            # ----------------------------------------------------------------------
            affix_blacklist = []

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

    // Behavior:
    //  - first apply during InterModEnqueueEvent, over IMC since that's the only path that works pre-game
    public static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        process((item, categoryName) -> CompatImc.send(item, categoryName));
    }

    // Behavior:
    //  - reapply for /apothiccompat reload. IMC is dead after load so write straight to Apoth's live
    //    override map and mirror into AdventureModule.IMC_TYPE_OVERRIDES by reflection or the entries
    //    vanish when AdventureConfig.load recopies on the next Apoth reload.
    //  - always re-reads the toml. /apothiccompat reload is operator-invoked, so re-applying every time
    //    is the intended behavior (no mtime gate; fast successive writes can leave mtime unchanged).
    //  - additive only. dropping a toml entry then reloading keeps the live override, so restart to drop it.
    // Returns:
    //  - ReloadResult holding the applied override count and the disabled-affix count
    public static ReloadResult reload() {
        Map<ResourceLocation, LootCategory> imcMirror = getImcOverrideMap();
        int count = process((item, categoryName) -> {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            LootCategory cat = LootCategory.byId(categoryName);
            if (id == null || cat == null) return;
            AdventureConfig.TYPE_OVERRIDES.put(id, cat);
            if (imcMirror != null) imcMirror.put(id, cat);
        });
        int disabled = loadAffixBlacklist();
        ApothicCompat.LOGGER.info("Apothic Compat config reloaded. Affix blacklist applied: {} affix(es) disabled.", disabled);
        return ReloadResult.ofApplied(count, disabled);
    }

    // Behavior:
    //  - reads affix_blacklist from the toml and reapplies it to Apoth's affix pool.
    //  - only safe after affixes have loaded (server start, datapack reload, /apothiccompat reload),
    //    never during the early IMC pass while the affix registry is still empty.
    // Returns:
    //  - the number of affixes actually disabled by the apply pass
    public static int loadAffixBlacklist() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        Set<ResourceLocation> ids = Set.of();
        try (CommentedFileConfig config = CommentedFileConfig.builder(path).sync().build()) {
            loadTolerant(config);
            ids = readAffixBlacklist(config);
        } catch (Exception e) {
            ApothicCompat.LOGGER.error("Failed to read affix blacklist from {}", FILE_NAME, e);
        }
        AffixBlacklist.setBlacklist(ids);
        return AffixBlacklist.apply();
    }

    public record ReloadResult(int count, int disabled) {
        public static ReloadResult ofApplied(int count, int disabled) { return new ReloadResult(count, disabled); }
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
            // NightConfig throws this when the file ends without a trailing newline after a table
            // header like [tag_overrides]. everything above the EOF is already parsed by then so it's
            // fine to keep going.
            if (e.getMessage() != null && e.getMessage().contains("Not enough data available")) {
                ApothicCompat.LOGGER.debug("Tolerating trailing-EOF parse hiccup in {}: {}", FILE_NAME, e.getMessage());
            } else {
                throw e;
            }
        }
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
            Item item = RegistryLookup.item(id);
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

    private static Set<ResourceLocation> readAffixBlacklist(CommentedFileConfig config) {
        Object raw = config.get("affix_blacklist");
        if (raw == null) return Set.of();
        if (!(raw instanceof List<?> list)) {
            ApothicCompat.LOGGER.warn("[affix_blacklist] must be an array of affix ids, got {}", raw);
            return Set.of();
        }
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (Object entry : list) {
            if (!(entry instanceof String s)) {
                ApothicCompat.LOGGER.warn("[affix_blacklist] entries must be strings, got {}", entry);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(s);
            if (id == null) {
                ApothicCompat.LOGGER.warn("[affix_blacklist] invalid affix id '{}'", s);
                continue;
            }
            ids.add(id);
        }
        return ids;
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
