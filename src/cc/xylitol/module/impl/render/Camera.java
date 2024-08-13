package cc.xylitol.module.impl.render;

import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.util.ResourceLocation;

public final class Camera extends Module {
    public static Camera INSTANCE;

    public Camera() {
        super("Camera", Category.Render);
        INSTANCE = this;
    }

    public final ModeValue capeMode = new ModeValue("Cape", new String[]{"Viper", "RBW", "Optifine"}, "RBW");
    public final BoolValue cameraClipValue = new BoolValue("CameraClip", false);
    public final BoolValue noHurtCameraValue = new BoolValue("NoHurtCamera", false);
    public final BoolValue betterBobbingValue = new BoolValue("BetterBobbing", false);
    public final BoolValue noFovValue = new BoolValue("NoFov", false);
    public final NumberValue fovValue = new NumberValue("Fov", 1.0, 0.0, 4.0, 0.1);

    public ResourceLocation getCape() {
        return new ResourceLocation("xylitol/images/cape/" + capeMode.get().toLowerCase() + ".png");
    }

    public boolean isOptifineCape() {
        return capeMode.is("Optifine");
    }
}