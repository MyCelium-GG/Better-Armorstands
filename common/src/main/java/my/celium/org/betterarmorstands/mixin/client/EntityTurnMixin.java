package my.celium.org.betterarmorstands.mixin.client;

import my.celium.org.betterarmorstands.client.StandClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** While R turns a stand, the mouse turns the stand instead of the camera. */
@Mixin(Entity.class)
abstract class EntityTurnMixin {
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void betterarmorstands$turnStand(double yRot, double xRot, CallbackInfo ci) {
        if ((Object) this == Minecraft.getInstance().player && StandClient.isTurning()) {
            StandClient.addMouse(yRot);
            ci.cancel();
        }
    }
}
