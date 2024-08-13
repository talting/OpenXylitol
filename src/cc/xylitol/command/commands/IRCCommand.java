package cc.xylitol.command.commands;

import cc.xylitol.command.Command;
import top.fl0wowp4rty.phantomshield.annotations.Native;

import java.util.ArrayList;
import java.util.List;

public class IRCCommand extends Command {
    public IRCCommand() {
        super("irc");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }

    @Override
    @Native
    public void run(String[] args, String originalMessag) {
    }
}