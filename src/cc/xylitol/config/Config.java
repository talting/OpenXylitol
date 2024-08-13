package cc.xylitol.config;

import com.google.gson.JsonObject;

/**
 * @author ChengFeng
 * @since 2023/3/19
 */
public abstract class Config {

    private final String name;

    public Config(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract JsonObject saveConfig();

    public abstract void loadConfig(JsonObject object);
}
