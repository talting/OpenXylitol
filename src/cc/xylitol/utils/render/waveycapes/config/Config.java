package cc.xylitol.utils.render.waveycapes.config;

import cc.xylitol.utils.render.waveycapes.CapeMovement;
import cc.xylitol.utils.render.waveycapes.CapeStyle;
import cc.xylitol.utils.render.waveycapes.WindMode;

public class Config {
    public static final WindMode windMode = WindMode.NONE;
    public static final CapeStyle capeStyle = CapeStyle.SMOOTH;
    public static final CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;
    public static final int gravity = 25;
    public static final int heightMultiplier = 6;
}
