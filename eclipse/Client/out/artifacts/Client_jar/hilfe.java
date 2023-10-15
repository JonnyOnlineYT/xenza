package me.shidori.module.impl.combat;

import java.awt.Color;

import me.shidori.events.EventTarget;
import me.shidori.events.impl.TickEvent;
import me.shidori.events.impl.movement.JumpEvent;
import me.shidori.events.impl.movement.MotionEvent;
import me.shidori.events.impl.movement.MoveEvent;
import me.shidori.events.types.EventType;
import me.shidori.module.Category;
import me.shidori.module.Module;
import me.shidori.module.ModuleInfo;
import me.shidori.settings.Setting;
import me.shidori.settings.impl.BooleanSetting;
import me.shidori.settings.impl.DoubleSetting;
import me.shidori.settings.impl.MultiBooleanSetting;
import me.shidori.settings.impl.StringSetting;
import me.shidori.utils.RandomUtil;
import me.shidori.utils.RotationUtil;
import me.shidori.utils.TimeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@ModuleInfo(name = "KillAura", renderName = "Kill Aura", description = "Attacks Entities for you", category = Category.COMBAT)
public class KillAura extends Module {
    public DoubleSetting range = new DoubleSetting("Range", this, 3.0, 3.0, 6.0, 2);
    public DoubleSetting maxCPS = new DoubleSetting("Max CPS", this, 15, 1, 20, 0);
    public DoubleSetting minCPS = new DoubleSetting("Min CPS", this, 10, 1, 20, 0);
    public BooleanSetting keepSprint = new BooleanSetting("Keep Sprint", this, false);
    public BooleanSetting players = new BooleanSetting("Players", this, false, true);
    public BooleanSetting mobs = new BooleanSetting("Mobs", this, false, true);
    public BooleanSetting animals = new BooleanSetting("Animals", this, false, true);
    public BooleanSetting villagers = new BooleanSetting("Villagers", this, false, true);
    public BooleanSetting invisibles = new BooleanSetting("Invisibles", this, false, true);
    public BooleanSetting dead = new BooleanSetting("Dead", this, false, true);
    public MultiBooleanSetting targets = new MultiBooleanSetting("Targets", this, new Setting[]{players, mobs, animals, villagers, invisibles, dead});
    public StringSetting blockMode = new StringSetting("Blocking Mode", this, "None", new String[]{"None", "Fake", "Vanilla"});
    public static EntityLivingBase target;
    private final RotationUtil rotationUtil = new RotationUtil();
    public TimeUtil timer = new TimeUtil();
    public int cps = 20;
    public float[] rotations = new float[] {0,0};

    @EventTarget
    public void onEventJump(JumpEvent eventJump) {
        if (target != null) {
            eventJump.setYaw(RotationUtil.yaw);
        }
    }

    @EventTarget
    public void onmove(MoveEvent e) {
        if (target != null) {
            e.setYaw(RotationUtil.yaw);
        }
    }

    @EventTarget
    public void onUpdate(MotionEvent event) {
        if(event.getType() == EventType.PRE) {
            target = searchTargets();
            if (target != null) {
                rotations = RotationUtil.positionRotation(target.posX, target.posY, target.posZ, RotationUtil.yaw, RotationUtil.pitch, false);
                if (this.timer.reached((long) (1000 / cps))) {
                    cps = RandomUtil.nextSecureInt((int)minCPS.getValue(), (int)maxCPS.getValue());
                    mc.thePlayer.swingItem();
                    mc.playerController.attackEntity(mc.thePlayer, target);
                    this.timer.reset();
                }
                if(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof net.minecraft.item.ItemSword) {
                    switch (blockMode.getMode()) {
                        case "Vanilla": {
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
                            break;
                        }
                    }
                }
                event.setYaw(rotationUtil.rotateToYaw(20, RotationUtil.yaw, rotations[0]));
                event.setPitch(rotationUtil.rotateToPitch(20, RotationUtil.pitch, rotations[1]));
            }
            //mc.thePlayer.rotationYaw = rotations[0];
            //mc.thePlayer.prevRotationYaw = rotations[0];
            //mc.thePlayer.rotationYawHead = rotations[0];
            //mc.thePlayer.rotationPitch = rotations[1];
            //mc.thePlayer.prevRotationPitch = rotations[1];
        }
    }

    public void onDisable() {
        target = null;

    }

    public void onEnable() {}

    public EntityLivingBase searchTargets() {
        double range = this.range.getValue();
        EntityLivingBase closestPlayer = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity o : mc.theWorld.loadedEntityList) {
            if (o != mc.thePlayer && isAllowed(o)) {
                double dist = mc.thePlayer.getDistanceToEntity(o);
                if (dist < range) {
                    closestDist = Math.min(closestDist, dist);
                    if (closestDist == dist)
                        closestPlayer = (EntityLivingBase)o;
                }
            }
        }
        return closestPlayer;
    }

    private boolean isAllowed(Entity o) {
        if(!invisibles.isEnabled() && o.isInvisible()) {
            return false;
        }
        if(!dead.isEnabled() && o.isDead) {
            return false;
        }
        if(players.isEnabled() && o instanceof EntityPlayer) {
            return true;
        }
        if(animals.isEnabled() && o instanceof EntityAnimal) {
            return true;
        }
        if(mobs.isEnabled() && o instanceof EntityMob) {
            return true;
        }
        if(villagers.isEnabled() && o instanceof EntityVillager) {
            return true;
        }
        return false;
    }
}
