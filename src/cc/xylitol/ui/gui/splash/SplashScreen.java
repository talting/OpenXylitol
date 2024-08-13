package cc.xylitol.ui.gui.splash;

import cc.xylitol.ui.gui.splash.impl.GenshinImpactLoadingScreen;
import cc.xylitol.ui.gui.splash.utils.AsyncContextUtils;
import cc.xylitol.ui.gui.splash.utils.AsyncGLContentLoader;
import cc.xylitol.ui.gui.splash.utils.Interpolations;
import cc.xylitol.ui.gui.splash.utils.Rect;
import cc.xylitol.utils.render.RenderUtil;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjglx.opengl.Display;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author ImXianyu
 * @since 4/24/2023 9:57 AM
 */
public class SplashScreen {

    public static final Object renderLock = new Object();
    public static final Object finishLock = new Object();
    private static Minecraft mc = Minecraft.getMinecraft();
    private static final int backgroundColor = RenderUtil.hexColor(0, 0, 0, 255);
    private static final Random random = new Random();
    private static final LoadingScreenRenderer loadingScreenRenderer = getLoadingScreen();
    public static int progress = 0;
    public static String progressText = "";
    public static Thread splashThread;
    public static float alpha = 1;
    public static boolean waiting = false;
    private static boolean firstFrame = false;
    private static Throwable threadError;
    private static int max_texture_size = -1;

    public static boolean crashDetected = false;

    public static long subWindow;

    /**
     * choose a loading screen randomly.
     *
     * @return loading screen's instance
     */
    @SneakyThrows
    private static LoadingScreenRenderer getLoadingScreen() {
        return new GenshinImpactLoadingScreen();
    }

    @SneakyThrows
    public static void init() {
        // INIT LOL
        subWindow = AsyncContextUtils.createSubWindow();
        GLFW.glfwMakeContextCurrent(subWindow);
        GL.createCapabilities();

        splashThread = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {

                GLFW.glfwMakeContextCurrent(Display.getWindow());
                GL.createCapabilities();

                initGL();

                loadingScreenRenderer.init();


                while (true) {

                    if (Display.wasResized()) {
                        initGL();
                    }

                    if (Display.isCloseRequested()) {
                        System.exit(0);
                    }

                    glClear(GL_COLOR_BUFFER_BIT);

                    if (!firstFrame) {
                        firstFrame = true;
                        RenderUtil.setFrameDeltaTime(0);
                    }

                    synchronized (renderLock) {

                        GlStateManager.pushAttrib();

                        Interpolations.calcFrameDelta();

                        int width = Display.getWidth();
                        int height = Display.getHeight();
                        GlStateManager.matrixMode(GL11.GL_PROJECTION);
                        GlStateManager.loadIdentity();
                        GlStateManager.ortho(0.0D, Display.getWidth(), Display.getHeight(), 0.0D, 1000.0D, 3000.0D);
                        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                        GlStateManager.loadIdentity();
                        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.disableFog();
                        GlStateManager.disableDepth();
                        GlStateManager.enableTexture2D();
                        GlStateManager.enableAlpha();
                        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                        GL11.glEnable(GL_TEXTURE_2D);

                        loadingScreenRenderer.render(Display.getWidth(), Display.getHeight());

//                        Random random = new Random();
//                        Rect.draw(random.nextInt(width - 100), random.nextInt(height - 50), 100, 50, 0xff0090ff, Rect.RectType.EXPAND);

//                        System.out.println(progress);
                        if (progress != 100)
                            alpha = (Interpolations.interpBezier(alpha * 255, 0, 0.1f) * 0.003921568627451F);

                        Rect.draw(0, 0, width, height, RenderUtil.hexColor(0, 0, 0, (int) (alpha * 255)), Rect.RectType.EXPAND);

                        if (progress >= 90) {
                            RenderUtil.drawRectWH(0, 0, width, height, new Color(32, 32, 32).getRGB());
                            RenderUtil.drawLoadingCircle(width / 2, height / 2.0F + 25);
                        }

                        GlStateManager.enableAlpha();
                        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                        Display.update();
                        Display.sync(240);
                        GlStateManager.popAttrib();

                        if (waiting && loadingScreenRenderer.isLoadingScreenFinished() && AsyncGLContentLoader.isAllTasksFinished()) {

                            if (mc == null)
                                mc = Minecraft.getMinecraft();


                            mc.displayWidth = Display.getWidth();
                            mc.displayHeight = Display.getHeight();
                            mc.resize(mc.displayWidth, mc.displayHeight);
                            glClearColor(1, 1, 1, 1);
                            glEnable(GL_DEPTH_TEST);
                            glDepthFunc(GL_LEQUAL);
                            glEnable(GL_ALPHA_TEST);
                            glAlphaFunc(GL_GREATER, .1f);

                            GLFW.glfwMakeContextCurrent(0L);

                            synchronized (finishLock) {
                                finishLock.notifyAll();
                            }

                            break;
                        }


                    }
                }
            }

            private void initGL() {
                glClearColor((float) ((backgroundColor >> 16) & 0xFF) / 0xFF, (float) ((backgroundColor >> 8) & 0xFF) / 0xFF, (float) (backgroundColor & 0xFF) / 0xFF, 1);
                glDisable(GL_LIGHTING);
                glDisable(GL_DEPTH_TEST);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

        }, "Loading Screen Thread");

        splashThread.setUncaughtExceptionHandler((t, e) -> threadError = e);
        splashThread.start();
        checkThreadState();
    }

    private static void checkThreadState() {
        if (splashThread.getState() == Thread.State.TERMINATED || threadError != null) {
            throw new IllegalStateException("Loading Screen thread", threadError);
        }
    }

    @SneakyThrows
    public static void hide() {
//        hide = true;

        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.cullFace(1029);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        mc.displayWidth = Display.getWidth();
        mc.displayHeight = Display.getHeight();
        mc.resize(mc.displayWidth, mc.displayHeight);
        glClearColor(1, 1, 1, 1);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, .1f);

    }

    @SneakyThrows
    public static void notifyGameLoaded() {
        if (!crashDetected) {
            loadingScreenRenderer.onGameLoadFinishedNotify();

            waiting = true;
            synchronized (finishLock) {
                finishLock.wait();
            }

            Thread.sleep(500);

            GLFW.glfwMakeContextCurrent(Display.getWindow());
            GL.createCapabilities();
            hide();
        }
    }

    @SneakyThrows
    public static void show() {
//        hide = false;
        waiting = false;
        alpha = 1;

        synchronized (finishLock) {
            finishLock.notifyAll();
        }
    }

    private static int getMaxTextureSize() {
        if (max_texture_size != -1) return max_texture_size;
        for (int i = 0x4000; i > 0; i >>= 1) {
            GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            if (GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) != 0) {
                max_texture_size = i;
                return i;
            }
        }
        return -1;
    }


    @SneakyThrows
    public static void setProgress(int progress, String detail) {
        SplashScreen.progress = progress;
        SplashScreen.progressText = detail;

        System.out.print("[Startup] " + progress + " - " + detail + "\n");
    }

}
