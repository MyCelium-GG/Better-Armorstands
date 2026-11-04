package my.celium.org.betterarmorstands.client;

import com.mojang.blaze3d.platform.InputConstants;

import my.celium.org.betterarmorstands.BetterArmorstandsMod;
import my.celium.org.betterarmorstands.BetterArmorstandsText;
import my.celium.org.betterarmorstands.ClientHooks;
import my.celium.org.betterarmorstands.Poses;
import my.celium.org.betterarmorstands.TurnPacket;
import my.celium.org.network.MycelNetwork;
import my.celium.org.util.TextUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Client part: hold R while looking at an armor stand and move the mouse to turn it (the camera
 * stays still, sneaking snaps to 15 degree steps), and while sneaking at a stand the action bar
 * shows its pose and the next one.
 */
public final class StandClient {
    /** Registered by each loader. */
    public static final KeyMapping TURN_KEY = new KeyMapping("key.betterarmorstands.turn", InputConstants.KEY_R,
            KeyMapping.Category.GAMEPLAY);

    /** Mouse units to degrees, the same factor vanilla uses for the camera. */
    private static final float MOUSE_TO_DEGREES = 0.15F;
    private static final float SNAP = 15F;
    private static final double MAX_DISTANCE = 8;

    private static ArmorStand target;
    private static float yaw;
    private static float sent;
    private static int previewTicks;

    private StandClient() {
    }

    /** Whether mouse movement turns a stand instead of the camera right now. */
    public static boolean isTurning() {
        return target != null;
    }

    /** Called from the camera hook with the mouse's horizontal movement. */
    public static void addMouse(double dx) {
        yaw += (float) dx * MOUSE_TO_DEGREES;
    }

    /** Runs every client tick on every loader. */
    public static void tick(Minecraft minecraft) {
        LocalPlayer player = minecraft == null ? null : minecraft.player;
        if (player == null || my.celium.org.client.MycelScreens.current() != null || !BetterArmorstandsMod.isModEnabled()) {
            target = null;
            return;
        }
        if (TURN_KEY.isDown()) {
            turnTick(minecraft, player);
        } else {
            target = null;
            previewTick(minecraft, player);
        }
    }

    private static void turnTick(Minecraft minecraft, LocalPlayer player) {
        if (target == null) {
            ArmorStand looked = lookedAt(minecraft);
            if (looked == null || !ClientHooks.serverHasMod()) {
                return;
            }
            target = looked;
            yaw = looked.getYRot();
            sent = yaw;
        }
        if (target.isRemoved() || target.distanceToSqr(player) > MAX_DISTANCE * MAX_DISTANCE) {
            target = null;
            return;
        }
        float shown = player.isShiftKeyDown() ? Math.round(yaw / SNAP) * SNAP : yaw;
        shown = Mth.wrapDegrees(shown);
        // Turn the stand here right away; the server confirms the same angle.
        target.setYRot(shown);
        target.setYBodyRot(shown);
        target.setYHeadRot(shown);
        if (Math.abs(Mth.wrapDegrees(shown - sent)) > 0.05F) {
            sent = shown;
            MycelNetwork.sendToServer(BetterArmorstandsMod.turnPacket, new TurnPacket(target.getId(), shown));
        }
        player.sendOverlayMessage(TextUtil.empty()
                .append(TextUtil.muted("Turning  "))
                .append(TextUtil.hex(Math.floorMod(Math.round(shown), 360) + "°", TextUtil.BRIGHT))
                .append(TextUtil.faint(player.isShiftKeyDown() ? "  15° steps" : "  sneak for 15° steps")));
    }

    private static void previewTick(Minecraft minecraft, LocalPlayer player) {
        if (previewTicks > 0) {
            previewTicks--;
        }
        if (!player.isShiftKeyDown() || !player.getMainHandItem().isEmpty() || previewTicks > 0) {
            return;
        }
        ArmorStand looked = lookedAt(minecraft);
        if (looked == null || looked.isMarker() || !ClientHooks.serverHasMod()) {
            return;
        }
        previewTicks = 10;
        player.sendOverlayMessage(BetterArmorstandsText.preview(Poses.indexOf(looked), Poses.next(looked)));
    }

    private static ArmorStand lookedAt(Minecraft minecraft) {
        return minecraft.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof ArmorStand stand ? stand : null;
    }
}
