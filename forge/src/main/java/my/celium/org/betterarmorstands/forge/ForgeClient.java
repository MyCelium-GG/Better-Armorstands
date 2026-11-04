package my.celium.org.betterarmorstands.forge;

import my.celium.org.betterarmorstands.client.StandClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;

/** Forge client side: the R key and the client tick. Only loaded on clients. */
final class ForgeClient {
    private ForgeClient() {
    }

    static void register() {
        RegisterKeyMappingsEvent.BUS.addListener(event -> event.register(StandClient.TURN_KEY));
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> StandClient.tick(Minecraft.getInstance()));
    }
}
