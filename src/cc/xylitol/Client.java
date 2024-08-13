package cc.xylitol;

import cc.xylitol.command.CommandManager;
import cc.xylitol.config.ConfigManager;
import cc.xylitol.event.EventManager;
import cc.xylitol.manager.*;
import cc.xylitol.module.ModuleManager;
import cc.xylitol.ui.gui.splash.SplashScreen;
import cc.xylitol.ui.hud.HUDManager;
import cc.xylitol.ui.hud.impl.SessionInfo;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.vialoadingbase.ViaLoadingBase;
import net.viamcp.ViaMCP;
import org.lwjglx.opengl.Display;
import top.fl0wowp4rty.phantomshield.annotations.Native;
import top.fl0wowp4rty.phantomshield.annotations.license.Virtualization;
import top.fl0wowp4rty.phantomshield.api.User;

import java.lang.reflect.Field;

@Native
@Getter
public class Client {
    public static final sun.misc.Unsafe theUnsafe;
    public static String name = "Xylitol";
    public static String version = "1.6";
    public static Client instance;

    public static Minecraft mc = Minecraft.getMinecraft();

    static {
        Field f;
        try {
            f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            theUnsafe = (sun.misc.Unsafe) f.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public String user = "";
    public EventManager eventManager;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public ConfigManager configManager;
    public PacketManager packetManager;
    public SlotSpoofManager slotSpoofManager;
    //    public WallpaperEngine wallpaperEngine;
    public BlinkManager blinkManager;
    public HUDManager hudManager;
    public RotationManager rotationManager;
    public FallDistanceManager fallDistanceManager;
    public BanManager banManager;
    public FriendManager friendManager;
    public NukerManager nukerManager;
    public String ingameName;
    public boolean clientLoadFinished = false;
    public boolean canSendMotionPacket = true;

    public static String getIGN() { // 部分网易服务器session和ign对不上，比如雅虎宇宙
        return mc.thePlayer == null ? mc.getSession().getUsername() : mc.thePlayer.getName();
    }

    @Virtualization
    public void init() {
        //QQUtils.init();

        SplashScreen.setProgress(70, "Xylitol - Start");
        instance = this;
        System.out.println("Starting " + name + " " + version);

        SplashScreen.setProgress(90, "Xylitol - Managers");

        eventManager = new EventManager();

        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        configManager = new ConfigManager();

        hudManager = new HUDManager();
        rotationManager = new RotationManager();
        fallDistanceManager = new FallDistanceManager();
        packetManager = new PacketManager();
        slotSpoofManager = new SlotSpoofManager();
        blinkManager = new BlinkManager();

        banManager = new BanManager();
        friendManager = new FriendManager();
        nukerManager = new NukerManager();

        eventManager.register(this);
        eventManager.register(rotationManager);
        eventManager.register(fallDistanceManager);
        eventManager.register(banManager);
        eventManager.register(blinkManager);

        eventManager.register(packetManager);
        eventManager.register(nukerManager);
        moduleManager.init();

        commandManager.init();

        configManager.loadAllConfig();

        try {
            ViaMCP.create();

            // In case you want a version slider like in the Minecraft options, you can use this code here, please choose one of those:

            ViaMCP.INSTANCE.initAsyncSlider(); // For top left aligned slider
            ViaLoadingBase.getInstance().reload(ProtocolVersion.v1_12_2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        clientLoadFinished = true;
        this.user = User.INSTANCE.getUsername("Free User");
        Display.setTitle(name + " " + version + " - " + this.user);
        SessionInfo.startTime = (int) System.currentTimeMillis();


    }

    public void shutdown() {
        System.out.println("Client shutdown");
    }
}
