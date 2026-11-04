package my.celium.org.betterarmorstands.fabric;

import my.celium.org.betterarmorstands.client.StandClient;
import my.celium.org.fabric.FabricNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

/** Fabric client entrypoint: the R key and the client tick. */
public final class BetterArmorstandsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricNetworking.flushClient();
        KeyMappingHelper.registerKeyMapping(StandClient.TURN_KEY);
        ClientTickEvents.END_CLIENT_TICK.register(StandClient::tick);
    }
}
