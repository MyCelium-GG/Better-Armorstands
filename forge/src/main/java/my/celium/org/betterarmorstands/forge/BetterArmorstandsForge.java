package my.celium.org.betterarmorstands.forge;

import my.celium.org.betterarmorstands.BetterArmorstandsMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/** Forge entrypoint. Clicks, arms and redstone come in through the common mixins. */
@Mod(BetterArmorstandsMod.ID)
public final class BetterArmorstandsForge {
    public BetterArmorstandsForge(@SuppressWarnings("unused") FMLJavaModLoadingContext context) {
        if (!net.minecraftforge.fml.ModList.isLoaded("mycel")) {
            my.celium.org.betterarmorstands.MissingMycel.logOnce();
            net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent.BUS.addListener(event -> {
                if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                    my.celium.org.betterarmorstands.MissingMycel.tell(player);
                }
            });
            return;
        }
        my.celium.org.betterarmorstands.ModGate.open();
        BetterArmorstandsMod.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClient.register();
        }
    }
}
