package cc.xylitol.ui.hud;


import cc.xylitol.Client;
import cc.xylitol.ui.hud.impl.*;
import cc.xylitol.value.Value;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class HUDManager {

    public Map<String, HUD> hudObjects = new HashMap<>();

    public void init() {
        add(new ModuleList());
        add(new Watermark());
        add(new TargetHUD());
        add(new SessionInfo());
        add(new Effects());
        add(new Inventory());
        add(new Chest());
        add(new Scoreboard());
    }

    private void add(HUD hud) {
        hudObjects.put(hud.getClass().getSimpleName(), hud);
        for (final Field field : hud.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(hud);
                if (obj instanceof Value) hud.m.getValues().add((Value<?>) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Client.instance.moduleManager.getModuleMap().put(hud.getClass().getSimpleName(),hud.m);

    }
}
