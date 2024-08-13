package cc.xylitol.command;

import cc.xylitol.command.commands.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {
    @Getter
    private final List<Command> commands = new ArrayList<>();
    public String[] latestAutoComplete = new String[0];
    public String prefix = ".";

    public void init() {
        reg(new BindCommand());
        reg(new ToggleCommand());
        reg(new ConfigCommand());
        reg(new BindsCommand());
        reg(new HelpCommand());
        reg(new ValueCommand());
        reg(new IRCCommand());
        reg(new QQCommand());
    }

    public Collection<String> autoComplete(String currCmd) {
        String raw = currCmd.substring(1);
        String[] split = raw.split(" ");

        List<String> ret = new ArrayList<>();

        Command currentCommand = split.length >= 1 ? commands.stream().filter(cmd -> cmd.match(split[0])).findFirst().orElse(null) : null;

        if (split.length >= 2 || currentCommand != null && currCmd.endsWith(" ")) {

            if (currentCommand == null) return ret;

            String[] args = new String[split.length - 1];

            System.arraycopy(split, 1, args, 0, split.length - 1);

            List<String> autocomplete = currentCommand.autoComplete(args.length + (currCmd.endsWith(" ") ? 1 : 0), args);
            this.latestAutoComplete = autocomplete.size() > 0 && autocomplete.get(0).equals("none") ? new String[]{""} : autocomplete.toArray(new String[0]);
            return autocomplete == null ? new ArrayList<>() : autocomplete;
        } else if (split.length == 1) {
            for (Command command : commands) {
                ret.addAll(Arrays.asList(command.getNames()));
            }

            return ret.stream().map(str -> "." + str).filter(str -> str.toLowerCase().startsWith(currCmd.toLowerCase())).collect(Collectors.toList());
        }

        return ret;
    }

    public void reg(Command command) {
        commands.add(command);
    }

    public Command getCommand(String name) {
        for (Command c : commands) {
            for (String s : c.getNames()) {
                if (s.equals(name))
                    return c;
            }
        }
        return null;
    }
}
