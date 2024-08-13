package cc.xylitol.config.configs;

import cc.xylitol.Client;
import cc.xylitol.config.Config;
import cc.xylitol.ui.hud.HUD;
import com.google.gson.JsonObject;

/**
 * @author ChengFeng
 * @since 2023/3/20
 */
public class HudConfig extends Config {
    public HudConfig() {
        super("hud.json");
    }

    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();

        for (HUD hud : Client.instance.hudManager.hudObjects.values()) {
            JsonObject hudObject = new JsonObject();

            hudObject.addProperty("x", hud.getPosX());
            hudObject.addProperty("y", hud.getPosY());

            object.add(hud.getName(), hudObject);
        }

        return object;
    }

    @Override
    public void loadConfig(JsonObject object) {
        for (HUD hud : Client.instance.hudManager.hudObjects.values()) {
            if (object.has(hud.getName())) {
                JsonObject hudObject = object.get(hud.getName()).getAsJsonObject();

                hud.setPosX(hudObject.get("x").getAsInt());
                hud.setPosY(hudObject.get("y").getAsInt());
            }
        }
    }
}
