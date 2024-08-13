package cc.xylitol.module;

import cc.xylitol.utils.render.animation.impl.RippleAnimation;

public enum Category {
    Combat(Pages.COMBAT),
    Movement(Pages.MOVEMENT),
    Render(Pages.RENDER),
    World(Pages.WORLD),
    Player(Pages.PLAYER),
    Misc(Pages.MISC),
    HUD(Pages.HUD);
    public final Pages pages;

    Category(Pages pages) {
        this.pages = pages;
    }

    public enum Pages {
        COMBAT(true, false),
        MOVEMENT(true, false),
        RENDER(true, false),
        WORLD(true, false),
        PLAYER(true, false),
        MISC(true, false),
        HUD(true, true),
        CONFIGS(false, true);
        public final boolean module;
        public final boolean gradient;

        public final RippleAnimation animation;

        Pages(boolean module, boolean gradient) {
            this.module = module;
            this.gradient = gradient;
            this.animation = new RippleAnimation();
        }
    }

    public Pages[] getSubPages() {
        return Pages.values();
    }
}
