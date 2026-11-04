package my.celium.org.betterarmorstands;

import my.celium.org.util.TextUtil;
import net.minecraft.network.chat.Component;

/** The few texts the mod shows, in the shared MyCelium look. */
public final class BetterArmorstandsText {
    private BetterArmorstandsText() {
    }

    /** Action bar after a pose change: {@code Pose  Salute  (8/13)}. */
    public static Component pose(Poses.Named pose) {
        int number = Poses.ALL.indexOf(pose) + 1;
        return TextUtil.empty()
                .append(TextUtil.muted("Pose  "))
                .append(TextUtil.hex(pose.name(), TextUtil.BRIGHT))
                .append(TextUtil.faint("  " + number + "/" + Poses.ALL.size()));
    }

    /** Action bar while sneaking at a stand: {@code Salute  »  next: Riposte}. */
    public static Component preview(int current, Poses.Named next) {
        String now = current < 0 ? "Custom pose" : Poses.ALL.get(current).name();
        return TextUtil.empty()
                .append(TextUtil.hex(now, TextUtil.BRIGHT))
                .append(TextUtil.faint("  »  "))
                .append(TextUtil.muted("click for "))
                .append(TextUtil.hex(next.name(), TextUtil.BRIGHT));
    }
}
