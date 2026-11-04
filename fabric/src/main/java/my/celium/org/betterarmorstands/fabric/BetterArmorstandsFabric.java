package my.celium.org.betterarmorstands.fabric;

import my.celium.org.betterarmorstands.BetterArmorstandsMod;
import my.celium.org.fabric.FabricNetworking;
import net.fabricmc.api.ModInitializer;

/** Fabric entrypoint. Clicks, arms and redstone come in through the common mixins. */
public final class BetterArmorstandsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterArmorstandsMod.init();
        // The packet may be registered after Mycel's own entrypoint ran.
        FabricNetworking.flush();
    }
}
