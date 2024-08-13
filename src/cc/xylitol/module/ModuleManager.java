package cc.xylitol.module;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventClick;
import cc.xylitol.event.impl.events.EventKey;
import cc.xylitol.event.impl.events.EventRender2D;
import cc.xylitol.module.impl.combat.*;
import cc.xylitol.module.impl.misc.*;
import cc.xylitol.module.impl.move.*;
import cc.xylitol.module.impl.player.*;
import cc.xylitol.module.impl.render.*;
import cc.xylitol.module.impl.world.*;
import cc.xylitol.utils.DebugUtil;
import cc.xylitol.value.Value;
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@RegisterLock
public class ModuleManager {
    private final Map<String, Module> moduleMap = new HashMap<>();

    private boolean enabledNeededMod = true;

    public void init() {
        Client.instance.eventManager.register(this);
        Client.instance.hudManager.init();

        // combat
        addModule(new KillAura());
        addModule(new Velocity());
        addModule(new NoClickDelay());
        addModule(new SuperKnockBack());
        addModule(new AutoSoup());
        addModule(new AutoWeapon());
        addModule(new AntiFireBall());
        addModule(new BackTrack());
        addModule(new TickBase());
        addModule(new Criticals());
        addModule(new Gapple());
        addModule(new AutoRunaway());
        //addModule(new AntiKb());
        addModule(new Test());
        addModule(new CSGOAimbot());
        // movement
        addModule(new Sneak());
        addModule(new Sprint());
        addModule(new Eagle());
        addModule(new NoWeb());
        addModule(new NoLiquid());
        addModule(new GuiMove());
        addModule(new NoSlow());
        addModule(new TargetStrafe());
        addModule(new Fly());
        addModule(new FastLadder());
        // player
        addModule(new MidPearl());
        addModule(new InvCleaner());
        addModule(new ChestStealer());
        addModule(new FastPlace());
        addModule(new Blink());
        addModule(new SpeedMine());
        addModule(new AutoTool());
        addModule(new Phase());
        addModule(new NoFall());

        // world
        addModule(new Disabler());
        addModule(new Scaffold());
        addModule(new ChestAura());
        addModule(new Stuck());
        addModule(new AutoPearl());
        addModule(new PlayerTracker());
        addModule(new Test());
        addModule(new Ambience());

        // render
        addModule(new ClickGUI());
        addModule(new HUD());
        addModule(new Chams());
        addModule(new BlockAnimation());
        addModule(new Camera());
        addModule(new Projectile());
        addModule(new Health());
        addModule(new XRay());
        addModule(new KillEffect());
        addModule(new ItemPhysics());
        addModule(new ESP());
        addModule(new MotionBlur());
        addModule(new BlockESP());
        addModule(new Particles());

        // misc
        addModule(new AntiBot());
        addModule(new Teams());
        addModule(new Protocol());
        addModule(new AutoPlay());
        addModule(new OffHandAbuse());
        addModule(new ChatBypass());
        addModule(new KillInsults());
        addModule(new MemoryFix());
        addModule(new Spammer());
        addModule(new NameProtect());
        addModule(new Spammer());
        addModule(new MCF());
        addModule(new KeepContainer());

        sortModulesByName();
    }

    public void sortModulesByName() {
        List<Map.Entry<String, Module>> entryList = new ArrayList<>(moduleMap.entrySet());
        entryList.sort(Comparator.comparing(entry -> entry.getValue().getName()));

        moduleMap.clear();
        for (Map.Entry<String, Module> entry : entryList) {
            moduleMap.put(entry.getKey(), entry.getValue());
        }
    }


    public void addModule(Module module) {
        for (final Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(module);
                if (obj instanceof Value) module.getValues().add((Value) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        moduleMap.put(module.getClass().getSimpleName(), module);
    }

    public Map<String, Module> getModuleMap() {
        return moduleMap;
    }

    public <T extends Module> T getModule(Class<T> cls) {
        return cls.cast(moduleMap.get(cls.getSimpleName()));
    }

    public Module getModule(String name) {
        for (Module module : moduleMap.values()) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public boolean haveModules(Category category, String key) {
        return moduleMap.values().stream()
                .filter(module -> module.getCategory() == category)
                .anyMatch(module -> module.getName().toLowerCase().replaceAll(" ", "").contains(key));
    }

    @EventTarget
    public void onKey(EventKey e) {
        moduleMap.values().stream()
                .filter(module -> module.getKey() == e.getKey() && e.getKey() != -1)
                .forEach(Module::toggle);
    }

    @EventTarget
    public void onMouse(EventClick e) {
        moduleMap.values().stream()
                .filter(module -> module.getMouseKey() != -1 && module.getMouseKey() == e.getKey() && e.getKey() != -1)
                .forEach(Module::toggle);
    }

    public List<Module> getModsByPage(Category.Pages m) {
        return moduleMap.values().stream()
                .filter(module -> module.getCategory().pages == m)
                .collect(Collectors.toList());
    }

    public List<Module> getModsByCategory(Category m) {
        return moduleMap.values().stream()
                .filter(module -> module.getCategory() == m)
                .collect(Collectors.toList());
    }

    @EventTarget
    private void on2DRender(EventRender2D e) {
        if (this.enabledNeededMod) {
            this.enabledNeededMod = false;
            moduleMap.values().stream()
                    .filter(Module::isDefaultOn)
                    .forEach(module -> module.setState(true));
        }
    }
}
