package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.CancellableEvent;
import net.minecraft.entity.EntityLivingBase;

public class EventRLE extends CancellableEvent {

    private final EntityLivingBase entity;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float rotationYawHead;
    private float rotationPitch;
    private float chestRot;
    private float offset;

    private float fuckingTick;

    private final boolean pre;

    public EventRLE(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYawHead, float rotationPitch, float chestRot, float offset, float fuckingTick) {
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.rotationYawHead = rotationYawHead;
        this.rotationPitch = rotationPitch;
        this.chestRot = chestRot;
        this.offset = offset;
        this.fuckingTick = fuckingTick;
        pre = true;
    }

    public EventRLE(EntityLivingBase entity) {
        this.entity = entity;
        pre = false;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    public float getLimbSwing() {
        return this.limbSwing;
    }

    public void setLimbSwing(float limbSwing) {
        this.limbSwing = limbSwing;
    }

    public float getLimbSwingAmount() {
        return this.limbSwingAmount;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        this.limbSwingAmount = limbSwingAmount;
    }

    public float getAgeInTicks() {
        return this.ageInTicks;
    }

    public void setAgeInTicks(float ageInTicks) {
        this.ageInTicks = ageInTicks;
    }

    public float getRotationYawHead() {
        return this.rotationYawHead;
    }

    public void setRotationYawHead(float rotationYawHead) {
        this.rotationYawHead = rotationYawHead;
    }

    public float getRotationPitch() {
        return this.rotationPitch;
    }

    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }

    public float getOffset() {
        return this.offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getRotationChest() {
        return this.chestRot;
    }

    public void setRotationChest(float rotationChest) {
        this.chestRot = rotationChest;
    }

    public float getTick() {
        return fuckingTick;
    }

    public boolean isPre() {
        return pre;
    }

    public boolean isPost() {
        return !pre;
    }
}
