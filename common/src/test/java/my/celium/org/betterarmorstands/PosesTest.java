package my.celium.org.betterarmorstands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PosesTest {
    @BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    void thirteenDistinctPoses() {
        assertEquals(13, Poses.ALL.size());
        Set<String> ids = new HashSet<>();
        Set<Object> poses = new HashSet<>();
        for (Poses.Named pose : Poses.ALL) {
            ids.add(pose.id());
            poses.add(pose.pose());
        }
        assertEquals(13, ids.size());
        assertEquals(13, poses.size());
    }

    @Test
    void firstPoseIsVanillasDefault() {
        assertEquals(net.minecraft.world.entity.decoration.ArmorStand.ArmorStandPose.DEFAULT, Poses.ALL.get(0).pose());
    }

    @Test
    void signalStrengthPicksAPoseAndWraps() {
        assertEquals("none", Poses.forSignal(1).id());
        assertEquals("hero", Poses.forSignal(12).id());
        assertEquals("default", Poses.forSignal(13).id());
        assertEquals("solemn", Poses.forSignal(15).id());
        assertNotEquals(Poses.forSignal(2), Poses.forSignal(3));
    }
}
