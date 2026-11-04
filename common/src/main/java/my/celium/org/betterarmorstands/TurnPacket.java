package my.celium.org.betterarmorstands;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Client {@code ->} server: "turn armor stand {@code entityId} to {@code yaw} degrees". */
public record TurnPacket(int entityId, float yaw) implements CustomPacketPayload {
    public static final Type<TurnPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(BetterArmorstandsMod.ID, "turn"));
    public static final StreamCodec<FriendlyByteBuf, TurnPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TurnPacket::entityId,
            ByteBufCodecs.FLOAT, TurnPacket::yaw,
            TurnPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
