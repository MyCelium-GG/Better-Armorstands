package my.celium.org.betterarmorstands.mixin;

import my.celium.org.betterarmorstands.StandActions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Clicks (poses, base plate) and arms on new stands. */
@Mixin(ArmorStand.class)
abstract class ArmorStandMixin {
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    private void betterarmorstands$arms(EntityType<? extends ArmorStand> type, Level level, CallbackInfo ci) {
        StandActions.onCreated((ArmorStand) (Object) this);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void betterarmorstands$interact(Player player, InteractionHand hand, Vec3 location,
            CallbackInfoReturnable<InteractionResult> cir) {
        ArmorStand stand = (ArmorStand) (Object) this;
        if (stand.level().isClientSide() && !my.celium.org.betterarmorstands.ClientHooks.serverHasMod()) {
            return;
        }
        InteractionResult result = StandActions.interact(stand, player, hand);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    /** Data without a ShowArms value (like plain /summon) keeps the arms default instead of "no arms". */
    @Redirect(method = "readAdditionalSaveData", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/ValueInput;getBooleanOr(Ljava/lang/String;Z)Z"))
    private boolean betterarmorstands$armsDefault(ValueInput input, String key, boolean fallback) {
        if ("ShowArms".equals(key)) {
            return input.getBooleanOr(key, StandActions.armsByDefault());
        }
        return input.getBooleanOr(key, fallback);
    }
}
