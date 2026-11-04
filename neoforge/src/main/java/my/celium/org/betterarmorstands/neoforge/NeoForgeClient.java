package my.celium.org.betterarmorstands.neoforge;

import my.celium.org.betterarmorstands.client.StandClient;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

/** NeoForge client side: the R key and the client tick. Only loaded on clients. */
final class NeoForgeClient {
    private NeoForgeClient() {
    }

    static void register(IEventBus modBus) {
        modBus.addListener((RegisterKeyMappingsEvent event) -> event.register(StandClient.TURN_KEY));
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> StandClient.tick(Minecraft.getInstance()));
    }
}
