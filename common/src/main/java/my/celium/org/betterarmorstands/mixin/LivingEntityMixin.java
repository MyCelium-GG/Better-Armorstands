package my.celium.org.betterarmorstands.mixin;

import my.celium.org.betterarmorstands.StandActions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Armor stands do not override tick(), so the redstone check hooks LivingEntity's. */
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void betterarmorstands$redstone(CallbackInfo ci) {
        if ((Object) this instanceof ArmorStand stand && !stand.level().isClientSide() && stand.tickCount % 4 == 0) {
            StandActions.redstoneTick(stand);
        }
    }
}
