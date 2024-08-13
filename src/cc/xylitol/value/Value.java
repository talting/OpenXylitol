package cc.xylitol.value;

import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.impl.DecelerateAnimation;
import cc.xylitol.utils.render.animation.impl.EaseBackIn;
import cc.xylitol.utils.render.animation.impl.OutputAnimation;
import lombok.Getter;

public abstract class Value<V> {
    public final OutputAnimation numberAnim = new OutputAnimation(0);
    public final EaseBackIn easeBackIn = new EaseBackIn(200, 0.2F, 1.0F);

    public final DecelerateAnimation decelerateAnimation = new DecelerateAnimation(200, 1F);

    public float animation = 0f;
    @Getter
    public float height = 22f;

    protected final Dependency dependency;
    public V value;
    String name;

    public Value(String name, Dependency dependenc) {
        this.name = name;
        this.dependency = dependenc;
    }

    public Value(String name) {
        this.name = name;
        this.dependency = () -> Boolean.TRUE;
    }

    public String getName() {
        return this.name;
    }

    public V getValue() {
        return this.value;
    }
    public V get() {
        return this.value;
    }

    public void setValue(V val) {
        this.value = val;
    }
    public void set(V val) {
        this.value = val;
    }

    public abstract <T> T getConfigValue();

    public boolean isHidden() {
        return !isAvailable();
    }

    public boolean isAvailable() {
        return dependency != null && this.dependency.check();
    }

    @FunctionalInterface
    public interface Dependency {
        boolean check();
    }
}
