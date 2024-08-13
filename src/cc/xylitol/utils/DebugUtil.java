package cc.xylitol.utils;

import cc.xylitol.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;

import static net.minecraft.util.EnumChatFormatting.BLUE;
import static net.minecraft.util.EnumChatFormatting.RESET;

public class DebugUtil {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static void print(Object... debug) {
        if (isDev()) {
            String message = Arrays.toString(debug);
            mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
        }
    }

    public static void log(Object message) {
        String text = String.valueOf(message);
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void log(boolean prefix, Object message) {
        String text = EnumChatFormatting.BOLD + "[" + Client.name + "] " + RESET + " " + message;
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void log(String prefix, Object message) {
        String text = EnumChatFormatting.BOLD + "[" + Client.name + "-" + prefix + "] " + RESET + " " + message;
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void logIRC(String user, String message) {
        String text = EnumChatFormatting.GOLD + "[IRC] " + BLUE + user + ": " + RESET + message;
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void logcheater(String prefix, Object message) {
        String text = EnumChatFormatting.BOLD + "[" + Client.name + "-" + prefix + "] " + RESET + " " + message;
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    private static boolean isDev() {
        return true;
    }
}
