package my.celium.org.betterarmorstands.mixin.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.armorstand.ArmorStandModel;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Anchors the base plate: vanilla counter-turns it by the stand's yaw, but takes that yaw from a
 * different value than the one the body is drawn with, so the plate wobbles while a stand turns.
 * Counter-turning by the body's own angle keeps it perfectly still.
 */
@Mixin(ArmorStandModel.class)
abstract class ArmorStandModelMixin {
    @Shadow
    @Final
    private ModelPart basePlate;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;)V", at = @At("TAIL"))
    private void betterarmorstands$anchorBasePlate(ArmorStandRenderState state, CallbackInfo ci) {
        basePlate.yRot = (float) Math.toRadians(-((LivingEntityRenderState) state).bodyRot);
    }
}
