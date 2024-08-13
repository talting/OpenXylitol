package cc.xylitol.command.commands;

import cc.xylitol.Client;
import cc.xylitol.command.Command;
import cc.xylitol.module.Module;
import cc.xylitol.utils.DebugUtil;
import net.minecraft.util.EnumChatFormatting;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind", "b");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        String prefix = "";
        boolean flag = false;

        if (arg == 0 || args.length == 0) {
            flag = true;
        } else if (arg == 1) {
            flag = true;
            prefix = args[0];
        }

        if (flag) {
            String finalPrefix = prefix;

            return Client.instance.moduleManager.getModuleMap().values().stream().filter(mod -> mod.getName().toLowerCase().startsWith(finalPrefix)).map(Module::getName).collect(Collectors.toList());
        } else if (arg == 2) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("none");
            arrayList.add("show");
            return arrayList;
        } else return new ArrayList<>();
    }

    @Override
    public void run(String[] args, String originalMessag) {
        if (args.length == 2) {
            Module m = Client.instance.moduleManager.getModule(args[0]);
            if (m != null) {
                if (args[1].toUpperCase().startsWith("MOUSE")) {
                    int key = Mouse.getButtonIndex(args[1].toUpperCase().replace("MOUSE", "BUTTON"));
                    m.setMouseKey(key);
                    DebugUtil.log(EnumChatFormatting.GREEN + "Success bound " + m.getName() + " to " + Mouse.getButtonName(m.getMouseKey()) + "!");

                } else {
                    int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                    m.setKey(key);
                    DebugUtil.log(EnumChatFormatting.GREEN + "Success bound " + m.getName() + " to " + Keyboard.getKeyName(m.getKey()) + "!");
                }
            } else {
                DebugUtil.log(EnumChatFormatting.RED + "Module not found!");
            }
        } else {
            DebugUtil.log(EnumChatFormatting.RED + "Usage: bind <Module> <Key>");
        }
    }
}
