package my.celium.org.betterarmorstands.mixin.client;

import my.celium.org.betterarmorstands.client.StandClient;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The stand being turned is drawn at the mouse's latest angle, not one smoothed between ticks. */
@Mixin(ArmorStandRenderer.class)
abstract class ArmorStandRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ArmorStand;Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;F)V",
            at = @At("TAIL"))
    private void betterarmorstands$liveAngle(ArmorStand stand, ArmorStandRenderState state, float partialTick, CallbackInfo ci) {
        Float angle = StandClient.renderAngle(stand);
        if (angle != null) {
            ((LivingEntityRenderState) state).bodyRot = angle;
            state.yRot = angle;
        }
    }
}
