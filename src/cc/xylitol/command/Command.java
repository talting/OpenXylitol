package cc.xylitol.command;

import java.util.List;

public abstract class Command {
    private final String[] name;

    public Command(String... name){
        this.name = name;
    }
    public abstract List<String> autoComplete(int arg, String[] args);

    public String[] getNames() {
        return name;
    }

    public abstract void run(String[] args, String originalMessage);


    boolean match(String name) {
        for (String alias : this.name) {
            if (alias.equalsIgnoreCase(name)) return true;
        }
        return this.name[0].equalsIgnoreCase(name);
    }


}
