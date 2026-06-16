package com.nightwielder.apothiccategorycompat.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.nightwielder.apothiccategorycompat.ApothicCategoryCompat;
import com.nightwielder.apothiccategorycompat.compat.AffixBlacklist;
import com.nightwielder.apothiccategorycompat.compat.RegistryLookup;
import com.nightwielder.apothiccategorycompat.util.CompatImc;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public final class ApothicCategoryCompatConfig {
    private static final String FILE_NAME = "apothic_category_compat-common.toml";
    private static final String LEGACY_FILE_NAME = "apothic_compat-common.toml";

    private static final String DEFAULT_CONTENTS = """
            # Apothic Category Compat: user defined loot category overrides.
            #
            # Maps Minecraft items (or item tags) to Apotheosis loot categories so the
            # affinity system can roll the right gem/affix pools on modded gear that
            # Apotheosis does not categorize on its own. Entries here are sent to
            # Apotheosis via IMC at startup, alongside the mod's built in compat
            # modules. Edit this file then run /apothiccategorycompat reload (op 2) to apply
            # changes without restarting the server.
            #
            # Built in loot category names (Apotheosis 7.4.8):
            #   sword, heavy_weapon, trident, bow, crossbow, shield,
            #   helmet, chestplate, leggings, boots, pickaxe, shovel, none
            #
            # Other mods can register additional categories at startup. Any name
            # that resolves at apply time is accepted; unknown names are skipped
            # with a warning. Known third party categories from Fallen Gems &
            # Affixes:
            #   staffs           (requires Iron's Spellbooks)
            #   celestial_melee  (requires Celestisynth)
            #   celestial_ranged (requires Celestisynth)
            #
            # When an item is set in both adventure.cfg (Apotheosis's own Equipment
            # Type Overrides) and this config, Apothic Category Compat's value currently wins.
            # To keep an adventure.cfg value, leave that item out of this file.
            #
            # Keys MUST be quoted because item and tag IDs contain a ':' separator
            # (namespace:path), which TOML does not allow in bare keys.

            # ----------------------------------------------------------------------
            # Categorization settings.
            #
            # name_based_heavy_override (default true): name-based fixups in both directions. An id whose
            # path contains a heavy weapon name (greatsword, claymore, zweihander, etc.) is forced to
            # HEAVY_WEAPON, and an id whose path ends in "sword" is forced to SWORD, so the speed and damage
            # read can't misfile an obvious weapon. Heavy is checked first, so a greatsword stays heavy.
            # Disable to use pure speed and damage.
            #
            # weapon_pickaxes_as_heavy (default true): When enabled, items in the
            # dual-purpose pickaxe list (combat tools like the Void Forge, Infernal
            # Forge, Cube of Annihilation, and Blacksmith Gavels) categorize as
            # HEAVY_WEAPON instead of PICKAXE. Disable to categorize them as
            # PICKAXE instead.
            # ----------------------------------------------------------------------
            name_based_heavy_override = true
            weapon_pickaxes_as_heavy = true

            # ----------------------------------------------------------------------
            # Affix blacklist. Stops the listed affixes from rolling on newly
            # generated gear (loot drops, reforging, trades, gem application).
            # Existing items keep any affixes they already have; this only blocks
            # future rolls. Apotheosis's own datapack affix overrides still win.
            #
            #   value = array of affix ids, each "namespace:path"
            #           e.g. "apotheosis:sword/attribute/vampiric"
            #
            # Find affix ids from JEI tooltips on affixed gear, or from the files
            # under data/<namespace>/affixes/ inside a mod's jar. Edit this list then
            # run /apothiccategorycompat reload (op 2) to reapply without a restart.
            #
            # Example:
            #   affix_blacklist = ["apotheosis:sword/attribute/vampiric", "apotheosis:heavy_weapon/attribute/berserking"]
            # ----------------------------------------------------------------------
            affix_blacklist = []

            # ----------------------------------------------------------------------
            # Per item overrides.
            #   key   = full item id (namespace:path)
            #   value = loot category name from the list above
            #
            # Example:
            #   "ruins:greatsword" = "heavy_weapon"
            #   "simplyswords:greathammer" = "heavy_weapon"
            # ----------------------------------------------------------------------
            [item_overrides]

            # ----------------------------------------------------------------------
            # Per tag overrides. Every item carrying the tag receives the category.
            #   key   = full tag id (namespace:path)
            #   value = loot category name from the list above
            #
            # Tags are resolved at apply time, so datapack only tags that load with a
            # world may not be visible during early startup. /apothiccategorycompat reload
            # runs after world load, so tag expansion there sees datapack tags.
            #
            # Example:
            #   "simplyswords:greathammers" = "heavy_weapon"
            # ----------------------------------------------------------------------
            [tag_overrides]
            """;

    private static boolean nameBasedHeavyOverride = true;
    private static boolean weaponPickaxesAsHeavy = true;

    // Snapshot of the last applied state, so /apothiccategorycompat reload can skip a no-op reload and report a diff.
    private static int lastFileHash;
    private static long lastFileMtime;
    private static boolean reloadStateCaptured;
    private static boolean startupSnapshotTaken;
    private static boolean startupNameOverride;
    private static boolean startupWeaponPickaxes;
    private static Map<ResourceLocation, LootCategory> lastAppliedOverrides = Map.of();
    private static Set<ResourceLocation> lastAppliedBlacklist = Set.of();

    public record ReloadResult(String message, String warning, int count) {}

    private ApothicCategoryCompatConfig() {}

    public static boolean nameBasedHeavyOverride() {
        return nameBasedHeavyOverride;
    }

    public static boolean weaponPickaxesAsHeavy() {
        return weaponPickaxesAsHeavy;
    }

    // Reads the categorization toggles into static fields so UniversalCompat can read them during the
    // dispatch passes, which run before the per item config load. Defaults hold when a key is missing.
    public static void loadSettings() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        addMissingSettings(path);
        boolean[] toggles = readToggles(path);
        nameBasedHeavyOverride = toggles[0];
        weaponPickaxesAsHeavy = toggles[1];
        // The first read is what UniversalCompat applied at startup, so keep it to compare against on reload.
        if (!startupSnapshotTaken) {
            startupNameOverride = toggles[0];
            startupWeaponPickaxes = toggles[1];
            startupSnapshotTaken = true;
        }
    }

    // index 0 is name_based_heavy_override, index 1 is weapon_pickaxes_as_heavy.
    private static boolean[] readToggles(Path path) {
        boolean nameOverride = true;
        boolean pickaxesHeavy = true;
        try (CommentedFileConfig config = CommentedFileConfig.builder(path).sync().build()) {
            loadTolerant(config);
            nameOverride = config.getOrElse("name_based_heavy_override", true);
            pickaxesHeavy = config.getOrElse("weapon_pickaxes_as_heavy", true);
        } catch (Exception e) {
            ApothicCategoryCompat.LOGGER.error("Failed to read categorization settings from {}", FILE_NAME, e);
        }
        return new boolean[]{nameOverride, pickaxesHeavy};
    }

    private static long fileMtime(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return -1;
        }
    }

    private static int fileHash(Path path) {
        try {
            return Arrays.hashCode(Files.readAllBytes(path));
        } catch (IOException e) {
            return 0;
        }
    }

    // A config saved before these toggles existed never gets the keys, so add any missing toggle at top
    // level. A bare key after a [table] header would bind to that table instead of the root.
    private static void addMissingSettings(Path path) {
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(path));
            List<String> additions = new ArrayList<>();
            if (!hasTopLevelKey(lines, "name_based_heavy_override")) {
                additions.add("name_based_heavy_override = true");
            }
            if (!hasTopLevelKey(lines, "weapon_pickaxes_as_heavy")) {
                additions.add("weapon_pickaxes_as_heavy = true");
            }
            if (additions.isEmpty()) {
                return;
            }
            additions.add(0, "# Categorization settings");
            additions.add(0, "");
            lines.addAll(settingsInsertIndex(lines), additions);
            Files.writeString(path, String.join("\n", lines) + "\n");
            ApothicCategoryCompat.LOGGER.info("Added missing categorization toggles to {}", FILE_NAME);
        } catch (IOException e) {
            ApothicCategoryCompat.LOGGER.error("Failed to add categorization toggles to {}", FILE_NAME, e);
        }
    }

    private static boolean hasTopLevelKey(List<String> lines, String key) {
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("[")) {
                return false;
            }
            if (trimmed.startsWith(key) && trimmed.substring(key.length()).trim().startsWith("=")) {
                return true;
            }
        }
        return false;
    }

    // Drop in after the last root level value and before the comment block that introduces the first table.
    private static int settingsInsertIndex(List<String> lines) {
        int i = lines.size();
        for (int j = 0; j < lines.size(); j++) {
            if (lines.get(j).trim().startsWith("[")) {
                i = j;
                break;
            }
        }
        while (i > 0) {
            String prev = lines.get(i - 1).trim();
            if (prev.isEmpty() || prev.startsWith("#")) {
                i--;
            } else {
                break;
            }
        }
        return i;
    }

    // First apply during InterModEnqueueEvent over IMC, the only path that works pre game. Item overrides
    // only: item tags aren't bound this early, so tag overrides are deferred to server start.
    public static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        Map<ResourceLocation, LootCategory> applied = new LinkedHashMap<>();
        processItems((item, categoryName) -> {
            CompatImc.send(item, categoryName);
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            LootCategory cat = LootCategory.byId(categoryName);
            if (id != null && cat != null) {
                applied.put(id, cat);
            }
        });
        lastAppliedOverrides = applied;
        lastFileMtime = fileMtime(path);
        lastFileHash = fileHash(path);
        reloadStateCaptured = true;
    }

    // Re-applies config overrides through the live override map after world load. By here datapack tags are
    // bound (the IMC pass can't see them) and the second pass has run, so a user's overrides land last and win.
    public static void applyOverridesAtRuntime() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        Map<ResourceLocation, LootCategory> imcMirror = getImcOverrideMap();
        Map<ResourceLocation, LootCategory> applied = new LinkedHashMap<>();
        process((item, categoryName) -> {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            LootCategory cat = LootCategory.byId(categoryName);
            if (id == null || cat == null) {
                return;
            }
            AdventureConfig.TYPE_OVERRIDES.put(id, cat);
            if (imcMirror != null) {
                imcMirror.put(id, cat);
            }
            applied.put(id, cat);
        });
        lastAppliedOverrides = applied;
        lastFileMtime = fileMtime(path);
        lastFileHash = fileHash(path);
        reloadStateCaptured = true;
    }

    // Reapply for /apothiccategorycompat reload. IMC is dead after load, so write to Apoth's live override map and
    // mirror into IMC_TYPE_OVERRIDES by reflection, or the entries vanish when AdventureConfig.load recopies
    // on the next Apoth reload. Skips when the file is byte for byte unchanged (mtime alone misses fast
    // writes, so the content hash backs it up). Additive only, so restart to drop a removed entry.
    public static ReloadResult reload() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        long mtime = fileMtime(path);
        int hash = fileHash(path);
        if (reloadStateCaptured && mtime == lastFileMtime && hash == lastFileHash) {
            return new ReloadResult("No changes detected.", null, lastAppliedOverrides.size());
        }

        Map<ResourceLocation, LootCategory> imcMirror = getImcOverrideMap();
        Map<ResourceLocation, LootCategory> applied = new LinkedHashMap<>();
        int[] tally = {0, 0, 0}; // new, changed, unchanged
        process((item, categoryName) -> {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            LootCategory cat = LootCategory.byId(categoryName);
            if (id == null || cat == null) {
                return;
            }
            AdventureConfig.TYPE_OVERRIDES.put(id, cat);
            if (imcMirror != null) {
                imcMirror.put(id, cat);
            }
            applied.put(id, cat);
            LootCategory prev = lastAppliedOverrides.get(id);
            if (prev == null) {
                tally[0]++;
            } else if (prev.equals(cat)) {
                tally[2]++;
            } else {
                tally[1]++;
            }
        });

        Set<ResourceLocation> prevBlacklist = lastAppliedBlacklist;
        int disabled = loadAffixBlacklist();
        int blAdded = countMissing(lastAppliedBlacklist, prevBlacklist);
        int blRemoved = countMissing(prevBlacklist, lastAppliedBlacklist);

        boolean[] toggles = readToggles(path);
        String warning = null;
        if (startupSnapshotTaken
                && (toggles[0] != startupNameOverride || toggles[1] != startupWeaponPickaxes)) {
            warning = "Note: categorization toggles changed since startup. Restart Minecraft to apply.";
        }

        lastAppliedOverrides = applied;
        lastFileMtime = mtime;
        lastFileHash = hash;
        reloadStateCaptured = true;

        int total = tally[0] + tally[1] + tally[2];
        String message = reloadMessage(total, tally[0], tally[1], tally[2], disabled, blAdded, blRemoved);
        ApothicCategoryCompat.LOGGER.info(message);
        return new ReloadResult(message, warning, total);
    }

    private static int countMissing(Set<ResourceLocation> from, Set<ResourceLocation> in) {
        int n = 0;
        for (ResourceLocation id : from) {
            if (!in.contains(id)) {
                n++;
            }
        }
        return n;
    }

    private static String reloadMessage(int total, int added, int changed, int unchanged,
                                        int disabled, int blAdded, int blRemoved) {
        StringBuilder sb = new StringBuilder("Config reloaded. ");
        if (total == 0) {
            sb.append("No item or tag overrides defined.");
        } else {
            sb.append("Applied ").append(total).append(total == 1 ? " override (" : " overrides (")
                    .append(added).append(" new, ").append(changed).append(" changed, ")
                    .append(unchanged).append(" unchanged).");
        }
        sb.append(" Affix blacklist: ").append(disabled).append(" disabled");
        if (blAdded > 0 || blRemoved > 0) {
            List<String> parts = new ArrayList<>();
            if (blAdded > 0) {
                parts.add(blAdded + " added");
            }
            if (blRemoved > 0) {
                parts.add(blRemoved + " removed");
            }
            sb.append(" (").append(String.join(", ", parts)).append(")");
        }
        sb.append(".");
        return sb.toString();
    }

    // Second pass after deferred mod init. The IMC window is closed, so swap CompatImc's sink to write
    // Apoth's live override map (mirroring IMC_TYPE_OVERRIDES like reload does), run the dispatch, then
    // restore the sink. Only items whose category changed from the first pass are written.
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

    // Reads affix_blacklist from the toml and reapplies it. Only safe once affixes have loaded (server
    // start, datapack reload), never during the early IMC pass while the registry is still empty.
    public static int loadAffixBlacklist() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        Set<ResourceLocation> ids = Set.of();
        try (CommentedFileConfig config = CommentedFileConfig.builder(path).sync().build()) {
            loadTolerant(config);
            ids = readAffixBlacklist(config);
        } catch (Exception e) {
            ApothicCategoryCompat.LOGGER.error("Failed to read affix blacklist from {}", FILE_NAME, e);
        }
        AffixBlacklist.setBlacklist(ids);
        lastAppliedBlacklist = ids;
        return AffixBlacklist.apply();
    }

    // Reads the file and sends each valid (item, category) pair to action, returning the applied count.
    private static int process(BiConsumer<Item, String> action) {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        int[] count = {0};
        try (CommentedFileConfig config = CommentedFileConfig.builder(path).sync().build()) {
            loadTolerant(config);
            count[0] += processItemOverrides(config, action);
            count[0] += processTagOverrides(config, action);
        } catch (Exception e) {
            ApothicCategoryCompat.LOGGER.error("Failed to read {}", FILE_NAME, e);
        }
        return count[0];
    }

    // Item overrides only, for the early IMC pass before item tags are bound.
    private static int processItems(BiConsumer<Item, String> action) {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        int[] count = {0};
        try (CommentedFileConfig config = CommentedFileConfig.builder(path).sync().build()) {
            loadTolerant(config);
            count[0] += processItemOverrides(config, action);
        } catch (Exception e) {
            ApothicCategoryCompat.LOGGER.error("Failed to read {}", FILE_NAME, e);
        }
        return count[0];
    }

    private static void loadTolerant(CommentedFileConfig config) {
        try {
            config.load();
        } catch (ParsingException e) {
            // NightConfig throws this when the file ends without a trailing newline after a table
            // header like [tag_overrides]. Everything above the EOF is already parsed by then, so it's
            // fine to keep going.
            if (e.getMessage() != null && e.getMessage().contains("Not enough data available")) {
                ApothicCategoryCompat.LOGGER.debug("Tolerating trailing EOF parse hiccup in {}: {}", FILE_NAME, e.getMessage());
            } else {
                throw e;
            }
        }
    }

    private static void ensureDefaultFile(Path path) {
        migrateLegacyFile(path);
        if (Files.exists(path)) {
            return;
        }
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, DEFAULT_CONTENTS);
        } catch (IOException e) {
            ApothicCategoryCompat.LOGGER.error("Failed to create default {}", FILE_NAME, e);
        }
    }

    // The mod was renamed from apothic_compat, so move a pre-rename config to the new name once to keep the
    // user's settings. The new file wins if both exist, and after the move the old file is gone, so later
    // calls do nothing.
    private static void migrateLegacyFile(Path path) {
        Path legacy = path.resolveSibling(LEGACY_FILE_NAME);
        if (Files.exists(path) || !Files.exists(legacy)) {
            return;
        }
        try {
            Files.move(legacy, path);
            ApothicCategoryCompat.LOGGER.info("Migrated config from {} to {}", LEGACY_FILE_NAME, FILE_NAME);
        } catch (IOException e) {
            ApothicCategoryCompat.LOGGER.error("Failed to migrate {} to {}", LEGACY_FILE_NAME, FILE_NAME, e);
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
                ApothicCategoryCompat.LOGGER.warn("[item_overrides] Key '{}' must map to a string category, got {}", key, value);
                continue;
            }
            if (LootCategory.byId(categoryName) == null) {
                ApothicCategoryCompat.LOGGER.warn("[item_overrides] Unknown loot category '{}', skipping override for '{}'. Either misspelled or registered by a mod that isn't installed.", categoryName, key);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id == null) {
                ApothicCategoryCompat.LOGGER.warn("[item_overrides] Invalid item id '{}'", key);
                continue;
            }
            Item item = RegistryLookup.item(id);
            if (item == null) {
                ApothicCategoryCompat.LOGGER.info("[item_overrides] Item '{}' not present; skipping", key);
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
                ApothicCategoryCompat.LOGGER.warn("[tag_overrides] Key '{}' must map to a string category, got {}", key, value);
                continue;
            }
            if (LootCategory.byId(categoryName) == null) {
                ApothicCategoryCompat.LOGGER.warn("[tag_overrides] Unknown loot category '{}', skipping override for '{}'. Either misspelled or registered by a mod that isn't installed.", categoryName, key);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id == null) {
                ApothicCategoryCompat.LOGGER.warn("[tag_overrides] Invalid tag id '{}'", key);
                continue;
            }
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, id);
            ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
            if (tag.isEmpty()) {
                ApothicCategoryCompat.LOGGER.info("[tag_overrides] Tag '{}' empty or not yet bound; skipping", key);
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
        if (raw == null) {
            return Set.of();
        }
        if (!(raw instanceof List<?> list)) {
            ApothicCategoryCompat.LOGGER.warn("[affix_blacklist] Must be an array of affix ids, got {}", raw);
            return Set.of();
        }
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (Object entry : list) {
            if (!(entry instanceof String s)) {
                ApothicCategoryCompat.LOGGER.warn("[affix_blacklist] Entries must be strings, got {}", entry);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(s);
            if (id == null) {
                ApothicCategoryCompat.LOGGER.warn("[affix_blacklist] Invalid affix id '{}'", s);
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
            ApothicCategoryCompat.LOGGER.warn("Could not access AdventureModule.IMC_TYPE_OVERRIDES; reload will not persist across Apotheosis config reloads", e);
            return null;
        }
    }
}
