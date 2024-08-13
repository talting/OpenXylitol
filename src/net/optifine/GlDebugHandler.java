package net.optifine;

import java.nio.IntBuffer;
import net.minecraft.src.Config;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjglx.LWJGLException;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjglx.opengl.*;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.system.MemoryUtil.NULL;

public class GlDebugHandler
{
    public static void createDisplayDebug() throws LWJGLException
    {
        boolean flag = GLContext.getCapabilities().GL_ARB_debug_output;
        ContextAttribs contextattribs = (new ContextAttribs()).withDebug(true);
        PixelFormat pixelFormat = (new PixelFormat()).withDepthBits(24);
        Display.create(pixelFormat, contextattribs);
        ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI) (source, type, id, severity, length, message, userParam) -> {
            handleMessage(source, type, id, severity, MemoryUtil.memASCII(message));
        }, NULL);
        ARBDebugOutput.glDebugMessageControlARB(4352, 4352, 4352, (IntBuffer)null, true);
        GL11.glEnable(33346);
    }

    public static void handleMessage(int source, int type, int id, int severity, String message)
    {
        if (!message.contains("glBindFramebuffer"))
        {
            if (!message.contains("Wide lines"))
            {
                if (!message.contains("shader recompiled"))
                {
                    Config.dbg("[LWJGL] source: " + getSource(source) + ", type: " + getType(type) + ", id: " + id + ", severity: " + getSeverity(severity) + ", message: " + message);
                    (new Throwable("StackTrace")).printStackTrace();
                }
            }
        }
    }

    public static String getSource(int source)
    {
        switch (source)
        {
            case 33350:
                return "API";

            case 33351:
                return "WIN";

            case 33352:
                return "SHADER";

            case 33353:
                return "EXT";

            case 33354:
                return "APP";

            case 33355:
                return "OTHER";

            default:
                return getUnknown(source);
        }
    }

    public static String getType(int type)
    {
        switch (type)
        {
            case 33356:
                return "ERROR";

            case 33357:
                return "DEPRECATED";

            case 33358:
                return "UNDEFINED";

            case 33359:
                return "PORTABILITY";

            case 33360:
                return "PERFORMANCE";

            case 33361:
                return "OTHER";

            default:
                return getUnknown(type);
        }
    }

    public static String getSeverity(int severity)
    {
        switch (severity)
        {
            case 37190:
                return "HIGH";

            case 37191:
                return "MEDIUM";

            case 37192:
                return "LOW";

            default:
                return getUnknown(severity);
        }
    }

    private static String getUnknown(int token) {
        return "Unknown (0x" + Integer.toHexString(token).toUpperCase() + ")";
    }
}
