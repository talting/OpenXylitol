package cc.xylitol.module.impl.render;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.render.animation.impl.ContinualAnimation;
import cc.xylitol.utils.sound.SoundUtil;
import cc.xylitol.value.impl.BoolValue;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;

public final class KillEffect extends Module {


    public KillEffect() {
        super("KillEffect", Category.Render);
    }

    private EntitySquid squid;
    private double percent = 0.0;
    private final ContinualAnimation anim = new ContinualAnimation();

    private final BoolValue lightning = new BoolValue("Lightning", true);

    private final BoolValue explosion = new BoolValue("Explosion", true);
    private final BoolValue squidValue = new BoolValue("Squid", true);
    private final BoolValue bloodValue = new BoolValue("Blood", true);

    private EntityLivingBase target;

    @EventTarget
    public void onMotion(EventMotion event) {

        if (squidValue.getValue()) {
            if (squid != null) {
                if (mc.theWorld.loadedEntityList.contains(squid)) {
                    if (percent < 1) percent += Math.random() * 0.048;
                    if (percent >= 1) {
                        percent = 0.0;
                        for (int i = 0; i <= 8; i++) {
                            mc.effectRenderer.emitParticleAtEntity(squid, EnumParticleTypes.FLAME);
                        }
                        mc.theWorld.removeEntity(squid);
                        squid = null;
                        return;
                    }
                } else {
                    percent = 0.0;
                }
                double easeInOutCirc = easeInOutCirc(1 - percent);
                anim.animate((float) easeInOutCirc, 450);
                squid.setPositionAndUpdate(squid.posX, squid.posY + anim.getOutput() * 0.9, squid.posZ);
            }

            if (squid != null) {
                squid.squidPitch = 0F;
                squid.prevSquidPitch = 0F;
                squid.squidYaw = 0F;
                squid.squidRotation = 90F;
            }
        }
        if (this.target != null && !mc.theWorld.loadedEntityList.contains(this.target)) {
            if (this.lightning.getValue()) {
                final EntityLightningBolt entityLightningBolt = new EntityLightningBolt(mc.theWorld, target.posX, target.posY, target.posZ);
                mc.theWorld.addEntityToWorld((int) (-Math.random() * 100000), entityLightningBolt);
                SoundUtil.playSound("ambient.weather.thunder");
            }

            if (this.explosion.getValue()) {
                for (int i = 0; i <= 8; i++) {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.FLAME);
                }

                SoundUtil.playSound("item.fireCharge.use");
            }

            if (this.squidValue.getValue()) {
                squid = new EntitySquid(mc.theWorld);
                mc.theWorld.addEntityToWorld(-8, squid);
                squid.setPosition(target.posX, target.posY, target.posZ);
            }
            if (this.bloodValue.getValue()) {
                mc.theWorld.spawnParticle(
                        EnumParticleTypes.BLOCK_CRACK,
                        target.posX,
                        target.posY + target.height - 0.75,
                        target.posZ,
                        0.0,
                        0.0,
                        0.0,
                        Block.getStateId(Blocks.redstone_block.getDefaultState())
                );
            }
            this.target = null;
        }
    }

    public double easeInOutCirc(double x) {
        return x < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        final Entity entity = event.getTarget();

        if (entity instanceof EntityLivingBase) {
            target = (EntityLivingBase) entity;
        }
    }

    ;
}