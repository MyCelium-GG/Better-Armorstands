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

    /** Ticks after letting go of R during which server rotation updates are ignored. */
    private static final int SETTLE_TICKS = 20;

    private static ArmorStand target;
    private static float yaw;
    private static float sent;
    private static int previewTicks;
    /** The stand turned last, and how long ago R was released. */
    private static int settlingId = -1;
    private static int settleTicks;
    /** The stand the hint was shown for, so looking at one stand shows it once. */
    private static int hintedId = -1;
    private static int hintCooldown;

    private StandClient() {
    }

    /** Whether mouse movement turns a stand instead of the camera right now. */
    public static boolean isTurning() {
        return target != null;
    }

    /** Called from the camera hook with the mouse's horizontal movement, every frame it moves. */
    public static void addMouse(double dx) {
        yaw += (float) dx * MOUSE_TO_DEGREES;
        ArmorStand stand = target;
        if (stand != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            apply(stand, shown(player != null && player.isShiftKeyDown()));
        }
    }

    /** Whether server rotation updates for {@code entityId} should be ignored right now. */
    public static boolean holdsRotation(int entityId) {
        ArmorStand stand = target;
        return stand != null && stand.getId() == entityId || entityId == settlingId && settleTicks > 0;
    }

    private static float shown(boolean snap) {
        return Mth.wrapDegrees(snap ? Math.round(yaw / SNAP) * SNAP : yaw);
    }

    /** Sets the stand's rotation with no interpolation, so it follows the mouse exactly. */
    private static void apply(ArmorStand stand, float angle) {
        // A server update that was already sliding the stand would otherwise pull it back.
        if (stand instanceof Interpolating interpolating) {
            interpolating.betterarmorstands$stopInterpolation();
        }
        stand.setYRot(angle);
        stand.yRotO = angle;
        stand.setYBodyRot(angle);
        stand.yBodyRotO = angle;
        stand.setYHeadRot(angle);
        stand.yHeadRotO = angle;
    }

    /** Runs every client tick on every loader. */
    public static void tick(Minecraft minecraft) {
        LocalPlayer player = minecraft == null ? null : minecraft.player;
        if (player == null || my.celium.org.client.MycelScreens.current() != null || !BetterArmorstandsMod.isModEnabled()) {
            target = null;
            return;
        }
        if (settleTicks > 0) {
            settleTicks--;
        }
        if (TURN_KEY.isDown()) {
            turnTick(minecraft, player);
        } else {
            if (target != null) {
                settlingId = target.getId();
                settleTicks = SETTLE_TICKS;
                target = null;
            }
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
        float shown = shown(player.isShiftKeyDown());
        // Keeps the stand where the mouse put it; the server gets the same angle below.
        apply(target, shown);
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
        if (hintCooldown > 0) {
            hintCooldown--;
        }
        ArmorStand looked = lookedAt(minecraft);
        if (looked == null || looked.isMarker() || !ClientHooks.serverHasMod()) {
            if (looked == null && hintCooldown == 0) {
                hintedId = -1;
            }
            return;
        }
        if (!player.isShiftKeyDown()) {
            // Once per stand you look at: how to turn it and how to pose it.
            if (looked.getId() != hintedId) {
                hintedId = looked.getId();
                hintCooldown = 100;
                player.sendOverlayMessage(TextUtil.empty()
                        .append(TextUtil.muted("Hold "))
                        .append(TextUtil.hex(TURN_KEY.getTranslatedKeyMessage().getString(), TextUtil.BRIGHT))
                        .append(TextUtil.muted(" to turn"))
                        .append(TextUtil.faint("  \u00B7  "))
                        .append(TextUtil.muted("Sneak + click: next pose")));
            }
            return;
        }
        if (!player.getMainHandItem().isEmpty() || previewTicks > 0) {
            return;
        }
        previewTicks = 10;
        player.sendOverlayMessage(BetterArmorstandsText.preview(Poses.indexOf(looked), Poses.next(looked)));
    }

    private static ArmorStand lookedAt(Minecraft minecraft) {
        return minecraft.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof ArmorStand stand ? stand : null;
    }
}
