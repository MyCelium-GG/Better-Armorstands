package my.celium.org.betterarmorstands.neoforge;

import my.celium.org.betterarmorstands.BetterArmorstandsMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

/** NeoForge entrypoint. Clicks, arms and redstone come in through the common mixins. */
@Mod(BetterArmorstandsMod.ID)
public final class BetterArmorstandsNeoForge {
    public BetterArmorstandsNeoForge(IEventBus modBus, @SuppressWarnings("unused") ModContainer container) {
        if (!net.neoforged.fml.ModList.get().isLoaded("mycel")) {
            my.celium.org.betterarmorstands.MissingMycel.logOnce();
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                    (net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) -> {
                if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                    my.celium.org.betterarmorstands.MissingMycel.tell(player);
                }
            });
            return;
        }
        my.celium.org.betterarmorstands.ModGate.open();
        BetterArmorstandsMod.init();
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NeoForgeClient.register(modBus);
        }
    }
}
