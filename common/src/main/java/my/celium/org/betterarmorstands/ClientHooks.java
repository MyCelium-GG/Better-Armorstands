package my.celium.org.betterarmorstands;

import my.celium.org.network.MycelNetwork;

/** Small checks the common mixins make on the client side. */
public final class ClientHooks {
    private ClientHooks() {
    }

    /** Whether the server we play on has the mod, so predicting our clicks is safe. */
    public static boolean serverHasMod() {
        try {
            return BetterArmorstandsMod.turnPacket != null && MycelNetwork.canSendToServer(BetterArmorstandsMod.turnPacket);
        } catch (RuntimeException | LinkageError e) {
            return false;
        }
    }
}
