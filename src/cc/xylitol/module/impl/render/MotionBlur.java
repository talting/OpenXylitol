package cc.xylitol.module.impl.render;


import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.value.impl.NumberValue;

public class MotionBlur extends Module {

    public final NumberValue blurAmount = new NumberValue("Amount", 7, 0.0, 10.0, 0.1);


    public MotionBlur() {
        super("MotionBlur", Category.Render);
    }


}
