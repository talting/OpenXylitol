package cc.xylitol.ui.gui.splash.utils;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;
import org.lwjglx.opengl.Display;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;

public class AsyncContextUtils {

    public static long createSubWindow() {

        GLFW.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        return GLFW.glfwCreateWindow(1, 1, "SubWindow", MemoryUtil.NULL, Display.getWindow());
    }

}
