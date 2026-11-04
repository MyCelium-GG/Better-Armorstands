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
        BetterArmorstandsMod.init();
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NeoForgeClient.register(modBus);
        }
    }
}
