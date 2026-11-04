package my.celium.org.betterarmorstands;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Everything that changes an armor stand. Runs on the server; the client only predicts the click. */
public final class StandActions {
    /** Farther than this, a turn request is ignored (a little more than entity reach). */
    private static final double TURN_REACH = 8;
    /** The last redstone signal each stand saw, so a steady signal does not fight manual poses. */
    private static final Map<ArmorStand, Integer> SIGNALS = new WeakHashMap<>();

    private StandActions() {
    }

    /** What a click on a stand does, or {@code null} to leave it to vanilla. */
    public enum Click {
        POSE, BASE_PLATE
    }

    /** Which of our actions a click means, without doing it. Same answer on both sides. */
    public static Click classify(ArmorStand stand, Player player, InteractionHand hand) {
        if (!BetterArmorstandsMod.isModEnabled() || hand != InteractionHand.MAIN_HAND || stand.isMarker()
                || player.isSpectator() || !player.mayBuild()) {
            return null;
        }
        ItemStack held = player.getMainHandItem();
        if (player.isShiftKeyDown() && held.isEmpty() && BetterArmorstandsMod.sneakPoses.get()) {
            return Click.POSE;
        }
        if (!player.isShiftKeyDown() && held.is(Items.SHEARS) && BetterArmorstandsMod.shearsBasePlate.get()) {
            return Click.BASE_PLATE;
        }
        return null;
    }

    /** Server side of a click: does it and returns the result, or {@code null} for vanilla behavior. */
    public static InteractionResult interact(ArmorStand stand, Player player, InteractionHand hand) {
        Click click = classify(stand, player, hand);
        if (click == null) {
            return null;
        }
        if (!stand.level().isClientSide()) {
            switch (click) {
                case POSE -> {
                    Poses.Named next = Poses.next(stand);
                    stand.setArmorStandPose(next.pose());
                    stand.playSound(SoundEvents.ARMOR_STAND_HIT, 0.5F, 1.4F);
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendOverlayMessage(BetterArmorstandsText.pose(next));
                    }
                    StandStats.count("pose_click");
                    StandStats.count("poses", next.id());
                }
                case BASE_PLATE -> {
                    stand.setNoBasePlate(stand.showBasePlate());
                    stand.level().playSound(null, stand.getX(), stand.getY(), stand.getZ(), SoundEvents.SHEEP_SHEAR,
                            SoundSource.PLAYERS, 0.8F, 1.2F);
                    StandStats.count(stand.showBasePlate() ? "baseplate_on" : "baseplate_off");
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    /** A new armor stand: arms, like Bedrock. Saved stands load their own value afterwards. */
    public static void onCreated(ArmorStand stand) {
        if (armsByDefault()) {
            stand.setShowArms(true);
        }
    }

    /** Whether stands without a saved arms value get arms. */
    public static boolean armsByDefault() {
        return BetterArmorstandsMod.armsByDefault != null && BetterArmorstandsMod.isModEnabled()
                && BetterArmorstandsMod.armsByDefault.get();
    }

    /** Every few ticks on the server: a changed redstone signal picks a pose. */
    public static void redstoneTick(ArmorStand stand) {
        if (!BetterArmorstandsMod.isModEnabled() || !BetterArmorstandsMod.redstonePoses.get() || stand.isMarker()) {
            return;
        }
        int signal = stand.level().getBestNeighborSignal(stand.blockPosition());
        Integer last = SIGNALS.put(stand, signal);
        if (last == null || last == signal || signal == 0) {
            return;
        }
        Poses.Named pose = Poses.forSignal(signal);
        stand.setArmorStandPose(pose.pose());
        StandStats.count("pose_redstone");
        StandStats.count("poses", pose.id());
    }

    /** A turn request from a player holding R. */
    public static void turn(ServerPlayer player, int entityId, float yaw) {
        if (player == null || !BetterArmorstandsMod.isModEnabled() || !BetterArmorstandsMod.allowTurning.get()
                || player.isSpectator() || !player.mayBuild() || !Float.isFinite(yaw)) {
            return;
        }
        Entity entity = player.level().getEntity(entityId);
        if (!(entity instanceof ArmorStand stand) || player.distanceToSqr(stand) > TURN_REACH * TURN_REACH
                || !player.level().mayInteract(player, stand.blockPosition())) {
            return;
        }
        float wrapped = net.minecraft.util.Mth.wrapDegrees(yaw);
        if (Math.abs(net.minecraft.util.Mth.wrapDegrees(stand.getYRot() - wrapped)) < 0.01F) {
            return;
        }
        stand.setYRot(wrapped);
        stand.setYBodyRot(wrapped);
        stand.setYHeadRot(wrapped);
        StandStats.turned(player);
    }
}
