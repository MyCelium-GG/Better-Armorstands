package my.celium.org.betterarmorstands;

import java.util.List;

import net.minecraft.core.Rotations;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ArmorStand.ArmorStandPose;

/** The 13 armor stand poses of Bedrock Edition, in Bedrock's order. */
public final class Poses {
    /** A pose with its name. */
    public record Named(String id, String name, ArmorStandPose pose) {
    }

    public static final List<Named> ALL = List.of(
            pose("default", "Default", r(0, 0, 0), r(0, 0, 0), r(-10, 0, -10), r(-15, 0, 10), r(-1, 0, -1), r(1, 0, 1)),
            pose("none", "No pose", r(0, 0, 0), r(0, 0, 0), r(0, 0, 0), r(0, 0, 0), r(0, 0, 0), r(0, 0, 0)),
            pose("solemn", "Solemn", r(15, 0, 0), r(0, 0, 2), r(-30, 15, 15), r(-60, -20, -10), r(-1, 0, -1), r(1, 0, 1)),
            pose("athena", "Athena", r(-5, 0, 0), r(0, 0, 2), r(10, 0, -5), r(-60, 20, -10), r(-3, -3, -3), r(3, 3, 3)),
            pose("brandish", "Brandish", r(-15, 0, 0), r(0, 0, -2), r(20, 0, -10), r(-110, 50, 0), r(5, -3, -3), r(-5, 3, 3)),
            pose("honor", "Honor", r(-15, 0, 0), r(0, 0, 0), r(-110, 35, 0), r(-110, -35, 0), r(5, -3, -3), r(-5, 3, 3)),
            pose("entertain", "Entertain", r(-15, 0, 0), r(0, 0, 0), r(-110, -35, 0), r(-110, 35, 0), r(5, -3, -3), r(-5, 3, 3)),
            pose("salute", "Salute", r(0, 0, 0), r(0, 0, 0), r(10, 0, -5), r(-70, -40, 0), r(-1, 0, -1), r(1, 0, 1)),
            pose("riposte", "Riposte", r(16, 20, 0), r(0, 0, 0), r(4, 8, 237), r(246, 0, 89), r(-14, -18, -16), r(8, 20, 4)),
            pose("zombie", "Zombie", r(-10, 0, -5), r(0, 0, 0), r(-105, 0, 0), r(-100, 0, 0), r(7, 0, 0), r(-46, 0, 0)),
            pose("cancan_a", "Can-can A", r(-5, 18, 0), r(0, 22, 0), r(8, 0, -114), r(0, 84, 111), r(-111, 55, 0), r(0, 23, -13)),
            pose("cancan_b", "Can-can B", r(-10, -20, 0), r(0, -18, 0), r(0, 0, -112), r(8, 90, 111), r(0, 0, 13), r(-119, -42, 0)),
            pose("hero", "Hero", r(-4, 67, 0), r(0, 8, 0), r(16, 32, -8), r(-99, 63, 0), r(0, -75, -8), r(4, 63, 8)));

    private Poses() {
    }

    private static Rotations r(float x, float y, float z) {
        return new Rotations(x, y, z);
    }

    private static Named pose(String id, String name, Rotations head, Rotations body, Rotations leftArm,
            Rotations rightArm, Rotations leftLeg, Rotations rightLeg) {
        return new Named(id, name, new ArmorStandPose(head, body, leftArm, rightArm, leftLeg, rightLeg));
    }

    /** Index of the stand's current pose, or {@code -1} for a custom one. */
    public static int indexOf(ArmorStand stand) {
        ArmorStandPose current = stand.getArmorStandPose();
        for (int i = 0; i < ALL.size(); i++) {
            if (ALL.get(i).pose().equals(current)) {
                return i;
            }
        }
        return -1;
    }

    /** The pose after the stand's current one; a custom pose goes back to the first. */
    public static Named next(ArmorStand stand) {
        int index = indexOf(stand);
        return ALL.get(index < 0 ? 0 : (index + 1) % ALL.size());
    }

    /** The pose a redstone signal of {@code strength} (1-15) picks. */
    public static Named forSignal(int strength) {
        return ALL.get(Math.floorMod(strength, ALL.size()));
    }
}
