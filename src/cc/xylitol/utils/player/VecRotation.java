package cc.xylitol.utils.player;

import net.minecraft.util.Vec3;
import org.lwjglx.util.vector.Vector2f;

class VecRotation {
    final Vec3 vec3;
    final Rotation rotation;

    public VecRotation(Vec3 Vec3, Rotation Rotation) {
        vec3 = Vec3;
        rotation = Rotation;
    }

    public Vec3 getVec3() {
        return vec3;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Vec3 getVec() {
        return vec3;
    }
}
