package my.celium.org.betterarmorstands.fabric;

import my.celium.org.betterarmorstands.BetterArmorstandsMod;
import my.celium.org.fabric.FabricNetworking;
import net.fabricmc.api.ModInitializer;

/** Fabric entrypoint. Clicks, arms and redstone come in through the common mixins. */
public final class BetterArmorstandsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("mycel")) {
            my.celium.org.betterarmorstands.MissingMycel.logOnce();
            net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register(
                    (handler, sender, server) -> my.celium.org.betterarmorstands.MissingMycel.tell(handler.getPlayer()));
            return;
        }
        my.celium.org.betterarmorstands.ModGate.open();
        BetterArmorstandsMod.init();
        // The packet may be registered after Mycel's own entrypoint ran.
        FabricNetworking.flush();
    }
}
