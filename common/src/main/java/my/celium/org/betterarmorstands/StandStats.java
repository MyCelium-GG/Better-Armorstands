package my.celium.org.betterarmorstands;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import my.celium.org.stats.ModStats;
import net.minecraft.server.level.ServerPlayer;

/**
 * Anonymous stats for the FastStats project {@code better-armorstands}, sent through Mycel under
 * its consent: which poses get used and how (click or redstone), how often stands are turned and
 * base plates toggled, and the settings.
 */
public final class StandStats {
    /** Project token from faststats.dev (public, ships in the jar). */
    private static final String TOKEN = "21604c01bde5cea9fef295cd8e29e555";

    private static volatile ModStats stats;
    /** Players who turned a stand recently, so one long R drag counts once. */
    private static final Map<UUID, Long> TURNING = new ConcurrentHashMap<>();

    private StandStats() {
    }

    static void start() {
        if (TOKEN.isEmpty()) {
            return;
        }
        stats = ModStats.builder(BetterArmorstandsMod.META, TOKEN)
                .bool("enabled", () -> BetterArmorstandsMod.enabled.get())
                .bool("sneak_poses", () -> BetterArmorstandsMod.sneakPoses.get())
                .bool("redstone_poses", () -> BetterArmorstandsMod.redstonePoses.get())
                .bool("arms_by_default", () -> BetterArmorstandsMod.armsByDefault.get())
                .bool("shears_base_plate", () -> BetterArmorstandsMod.shearsBasePlate.get())
                .bool("allow_turning", () -> BetterArmorstandsMod.allowTurning.get())
                .counters("poses")
                .errors("my.celium.org.betterarmorstands.")
                .start();
    }

    /** Counts a feature use. */
    static void count(String what) {
        ModStats current = stats;
        if (current != null) {
            current.count(what);
        }
    }

    /** Counts {@code key} in the counter map {@code id}. */
    static void count(String id, String key) {
        ModStats current = stats;
        if (current != null) {
            current.count(id, key);
        }
    }

    /** A turn packet arrived; counts one "turn" per drag (2 s without packets ends it). */
    static void turned(ServerPlayer player) {
        long now = System.currentTimeMillis();
        Long last = TURNING.put(player.getUUID(), now);
        if (last == null || now - last > 2000) {
            count("turn");
        }
    }
}
