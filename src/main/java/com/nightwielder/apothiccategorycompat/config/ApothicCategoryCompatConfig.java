package com.nightwielder.apothiccategorycompat.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.nightwielder.apothiccategorycompat.ApothicCategoryCompat;
import com.nightwielder.apothiccategorycompat.compat.AffixBlacklist;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Category overrides moved to a data map on 1.21.1, so the toml only holds the affix blacklist. Apotheosis
// rebuilds its affix pool on every reload, so this reapplies from server start and after /reload.
public final class ApothicCategoryCompatConfig {
    private static final String FILE_NAME = "apothic_category_compat-common.toml";
    private static final String LEGACY_FILE_NAME = "apothic_compat-common.toml";

    private static final String DEFAULT_CONTENTS = """
            # Route dual purpose pickaxe weapons (Cataclysm forges, Forbidden Arcanus gavels) to melee weapon. False keeps Apotheosis's breaker category.
            weapon_pickaxes_as_melee = true

            # Apothic Category Compat affix blacklist.
            #
            # Stops the listed affixes from rolling on newly generated gear (loot drops, reforging, trades,
            # gem application). Existing items keep any affixes they already have; this only blocks future
            # rolls. Apotheosis's own datapack affix overrides still win.
            #
            #   value = array of affix ids, each "namespace:path"
            #
            # Find affix ids from JEI tooltips on affixed gear, or from the files under
            # data/<namespace>/affixes/ inside a mod's jar. Edit this list then run /acc reload (op 2) to
            # reapply without a restart.
            #
            # Example:
            #   affix_blacklist = ["apotheosis:attribute/sword/vampiric"]

            affix_blacklist = []
            """;

    // Snapshot of the last applied state so /acc reload can skip a no-op and report what changed.
    private static int lastFileHash;
    private static long lastFileMtime;
    private static boolean reloadStateCaptured;
    private static Set<ResourceLocation> lastAppliedBlacklist = Set.of();

    public record ReloadResult(String message, int count) {}

    private ApothicCategoryCompatConfig() {}

    // Read live from the file: the data map condition tests this during datapack load, before the server
    // start and reload hooks run, so a cached field would not be populated in time for the first load.
    public static boolean weaponPickaxesAsMelee() {
        return readBoolean("weapon_pickaxes_as_melee", true);
    }

    // Only safe once affixes have loaded (server start, datapack reload), never while the registry is empty.
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

    // Reapply for /acc reload. Skips when the file is byte for byte unchanged since the last apply: the mtime
    // alone misses fast successive writes that leave it untouched, so the content hash backs it up.
    public static ReloadResult reload() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        long mtime = fileMtime(path);
        int hash = fileHash(path);
        if (reloadStateCaptured && mtime == lastFileMtime && hash == lastFileHash) {
            return new ReloadResult("No changes detected.", lastAppliedBlacklist.size());
        }

        Set<ResourceLocation> previous = lastAppliedBlacklist;
        int disabled = loadAffixBlacklist();
        int added = countMissing(lastAppliedBlacklist, previous);
        int removed = countMissing(previous, lastAppliedBlacklist);

        lastFileMtime = mtime;
        lastFileHash = hash;
        reloadStateCaptured = true;

        String message = reloadMessage(disabled, added, removed);
        ApothicCategoryCompat.LOGGER.info(message);
        return new ReloadResult(message, disabled);
    }

    private static String reloadMessage(int disabled, int added, int removed) {
        StringBuilder sb = new StringBuilder("Affix blacklist reloaded: ");
        sb.append(disabled).append(" disabled");
        if (added > 0 || removed > 0) {
            StringBuilder diff = new StringBuilder();
            if (added > 0) {
                diff.append(added).append(" added");
            }
            if (removed > 0) {
                if (diff.length() > 0) {
                    diff.append(", ");
                }
                diff.append(removed).append(" removed");
            }
            sb.append(" (").append(diff).append(")");
        }
        sb.append(".");
        return sb.toString();
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

    private static boolean readBoolean(String key, boolean fallback) {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        ensureDefaultFile(path);
        try (CommentedFileConfig config = CommentedFileConfig.builder(path).sync().build()) {
            loadTolerant(config);
            Object value = config.get(key);
            if (value instanceof Boolean bool) {
                return bool;
            }
            if (value != null) {
                ApothicCategoryCompat.LOGGER.warn("[{}] '{}' must be true or false, got {}; using {}", FILE_NAME, key, value, fallback);
            }
            return fallback;
        } catch (Exception e) {
            ApothicCategoryCompat.LOGGER.error("Failed to read {} from {}", key, FILE_NAME, e);
            return fallback;
        }
    }

    private static void loadTolerant(CommentedFileConfig config) {
        try {
            config.load();
        } catch (ParsingException e) {
            // NightConfig throws this when the file ends without a trailing newline after the last value.
            // Everything above the EOF is already parsed by then, so it's fine to keep going.
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
}
