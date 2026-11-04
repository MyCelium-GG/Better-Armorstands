package my.celium.org.betterarmorstands.mixin.client;

import my.celium.org.betterarmorstands.client.StandClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * While a stand is being turned (and briefly after), the server's slightly older rotation
 * updates for it are dropped, so they cannot pull the stand back against the mouse.
 */
@Mixin(Entity.class)
abstract class EntityInterpolationMixin {
    @Inject(method = {
            "moveOrInterpolateTo(Lnet/minecraft/world/entity/PositionPath;)V",
            "moveOrInterpolateTo(Lnet/minecraft/world/phys/Vec3;FF)V",
            "moveOrInterpolateTo(FF)V",
            "moveOrInterpolateTo(Lnet/minecraft/world/entity/PositionPath;FF)V"}, at = @At("HEAD"), cancellable = true)
    private void betterarmorstands$holdTurn(CallbackInfo ci) {
        if (StandClient.holdsRotation(((Entity) (Object) this).getId())) {
            ci.cancel();
        }
    }
}
