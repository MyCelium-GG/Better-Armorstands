package my.celium.org.betterarmorstands;

import my.celium.org.util.TextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** The few texts the mod shows, in the shared MyCelium look. */
public final class BetterArmorstandsText {
    private BetterArmorstandsText() {
    }

    /** Action bar after a pose change: {@code Pose  Salute  (8/13)}. */
    public static Component pose(Player player, Poses.Named pose) {
        int number = Poses.ALL.indexOf(pose) + 1;
        return TextUtil.empty()
                .append(TextUtil.muted(BetterArmorstandsMod.LANG.get(player, "pose") + "  "))
                .append(TextUtil.hex(name(player, pose), TextUtil.BRIGHT))
                .append(TextUtil.faint("  " + number + "/" + Poses.ALL.size()));
    }

    /** Action bar while sneaking at a stand: {@code Salute  »  next: Riposte}. */
    public static Component preview(Player player, int current, Poses.Named next) {
        String now = current < 0 ? BetterArmorstandsMod.LANG.get(player, "pose.custom") : name(player, Poses.ALL.get(current));
        return TextUtil.empty()
                .append(TextUtil.hex(now, TextUtil.BRIGHT))
                .append(TextUtil.faint("  »  "))
                .append(TextUtil.muted(BetterArmorstandsMod.LANG.get(player, "pose.click_for") + " "))
                .append(TextUtil.hex(name(player, next), TextUtil.BRIGHT));
    }

    /** A pose's name in the player's language. */
    public static String name(Player player, Poses.Named pose) {
        return BetterArmorstandsMod.LANG.get(player, "pose." + pose.id());
    }
}
