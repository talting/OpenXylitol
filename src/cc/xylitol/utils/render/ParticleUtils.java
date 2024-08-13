
package cc.xylitol.utils.render;

public final class ParticleUtils {

    private static final ParticleGenerator particleGenerator = new ParticleGenerator(50);

    public static void drawParticles(int mouseX, int mouseY) {
        particleGenerator.draw(mouseX, mouseY);
    }
}