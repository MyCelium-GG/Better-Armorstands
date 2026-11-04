package my.celium.org.betterarmorstands;

import my.celium.org.Mycel;
import my.celium.org.config.ConfigBuilder;
import my.celium.org.config.ConfigValue;
import my.celium.org.config.MycelConfig;
import my.celium.org.metadata.ModMetadata;
import my.celium.org.network.MycelNetwork;
import my.celium.org.network.PacketDefinition;

/**
 * Better Armorstands: the Bedrock poses (sneak + right-click, or a redstone signal), arms on new
 * armor stands, shears for the base plate, and with the mod on the client: hold R and move the
 * mouse to turn a stand, a pose preview while sneaking and a nicer texture.
 *
 * <p>Everything that changes a stand runs on the server ({@link StandActions}), so players
 * without the mod get the poses, arms and base plate too. The client part lives in
 * {@code client/}, which dedicated servers never load.
 */
public final class BetterArmorstandsMod {
    public static final String ID = "betterarmorstands";

    public static final ModMetadata META = ModMetadata.builder(ID, "Better Armorstands", "1.0.0")
            .author("MyCelium")
            .website("https://www.curseforge.com/minecraft/mc-mods/better-armorstands")
            .updateUrl("https://wiki.mycelium-gg.org/updates/betterarmorstands.json")
            .build();

    public static ConfigValue<Boolean> enabled;
    public static ConfigValue<Boolean> sneakPoses;
    public static ConfigValue<Boolean> redstonePoses;
    public static ConfigValue<Boolean> armsByDefault;
    public static ConfigValue<Boolean> shearsBasePlate;
    public static ConfigValue<Boolean> allowTurning;
    public static MycelConfig config;

    public static PacketDefinition<TurnPacket> turnPacket;

    private BetterArmorstandsMod() {
    }

    /** Called once from each loader's entrypoint. */
    public static void init() {
        Mycel.initialize();
        Mycel.registerMod(META);

        ConfigBuilder builder = Mycel.config(META);
        enabled = builder.booleanValue("enabled", true,
                "Master switch for Better Armorstands.");
        sneakPoses = builder.booleanValue("sneakPoses", true,
                "Sneak + right-click an armor stand with an empty hand to switch to the next pose.");
        redstonePoses = builder.booleanValue("redstonePoses", true,
                "A redstone signal next to an armor stand picks a pose by its strength (1 to 12, 13 and up wrap around).");
        armsByDefault = builder.booleanValue("armsByDefault", true,
                "New armor stands have arms, like in Bedrock Edition.");
        shearsBasePlate = builder.booleanValue("shearsBasePlate", true,
                "Right-click an armor stand with shears to remove or bring back its base plate.");
        allowTurning = builder.booleanValue("allowTurning", true,
                "Players with the mod may hold R and move the mouse to turn an armor stand.");
        config = builder.build();
        StandStats.start();

        turnPacket = PacketDefinition.toServer(TurnPacket.TYPE, TurnPacket.CODEC,
                (packet, context) -> StandActions.turn(context.sender(), packet.entityId(), packet.yaw()));
        // Optional both ways: a server without the mod never receives it, a client without it never sends it.
        MycelNetwork.register(META, turnPacket);
    }

    public static boolean isModEnabled() {
        return Mycel.isEnabled(ID) && enabled != null && enabled.get();
    }
}
