package net.augustus.modules.combat;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import net.augustus.Augustus;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.misc.MidClick;
import me.jDev.xenza.files.FileManager;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.BooleansSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.Setting;
import net.augustus.settings.StringValue;
import net.augustus.utils.*;
import net.augustus.utils.skid.rise6.BadPacket;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class KillAura extends Module {
   private final transient TimeHelper targetDelayTimer = new TimeHelper();
   private final TimeHelper hitTimeHelper = new TimeHelper();
   private final TimeHelper hittedTimeHelper = new TimeHelper();
   private final ArrayList<double[]> positions = new ArrayList<>();
   public static EntityLivingBase target = null;
   public float[] rots = new float[2];
   public float[] lastRots = new float[2];
   public RotationUtil rotationUtil;
   public double range = 3.0;
   public DoubleValue preRange;
   public DoubleValue rangeSetting;
   public DoubleValue targetDelay;
   public DoubleValue yawSpeedMin;
   public DoubleValue yawSpeedMax;
   public DoubleValue pitchSpeedMin;
   public DoubleValue pitchSpeedMax;
   public DoubleValue minDelay;
   public DoubleValue maxDelay;
   public BooleanValue gommeFix;
   public BooleanValue grimfunni;

   public DoubleValue hitChance;
   public DoubleValue randomStrength;
   public BooleanValue player;
   public BooleanValue mob;
   public BooleanValue animal;
   public BooleanValue villager;
   public BooleanValue armorStand;
   public BooleanValue invisible;
   public BooleanValue interpolation;
   public BooleanValue smoothBackRotate;
   public BooleanValue stopOnTarget;
   public BooleanValue perfectHit;
   public BooleanValue perfectHitGomme;
   public BooleanValue moveFix;
   public BooleanValue silentMoveFix;
   public BooleanValue bestHitVec;
   public BooleanValue smartAim;
   public BooleanValue throughWalls;
   public BooleanValue slowDown;
   public BooleanValue coolDown;
   public BooleanValue preHit;
   public BooleanValue inInv;
   public BooleanValue randomize;
   public BooleanValue block;
   public BooleanValue multi;
   public BooleanValue heuristics;
   public BooleanValue hazeRange;
   public DoubleValue hazeAdd;
   public DoubleValue hazeMax;
   public BooleanValue intave;
   public BooleanValue advancedRots;
   public StringValue mode;
   public BooleansSetting targets;
   public StringValue sortMode;
   public StringValue targetRandom;
   public StringValue rangeMode;
   public StringValue attackMode;
   public StringValue blockMode;
   public DoubleValue startBlock;
   public DoubleValue endBlock;
   public DoubleValue endBlockHitOnly;
   public BooleanValue unblockHit;
   public BooleanValue unblockHitOnly;
   public boolean backRotate = false;
   private final int multiClickCounter = 0;
   private long time = 50L;
   private EntityLivingBase lastTarget = null;
   private long randomDelay = 100L;
   private Vec3 best;
   private boolean ssBlocking;
   private Object[] recordedClicks;
   private final int counter = 0;
   private final long changeDelayDelay = 100L;
   private final long delaySpike = 2000L;
   private final long delayDrop = 2000L;
   private final double cpsCounter = 1.0;
   private final long randomCPS = 10L;
   private long lastTime;

   public KillAura() {
      super("KillAura", new Color(102, 38, 28, 255), Categorys.COMBAT);
      preRange = new DoubleValue(1, "PreAimRange", this, 4.0, 0.0, 15.0, 1);
      rangeSetting = new DoubleValue(2, "Range", this, 3.0, 3.0, 6.0, 2);
      targetDelay = new DoubleValue(3, "TargetDelay", this, 500.0, 0.0, 1000.0, 0);
      yawSpeedMin = new DoubleValue(64, "YawSpeedMin", this, 40.0, 1.0, 180.0, 0);
      yawSpeedMax = new DoubleValue(4, "YawSpeedMax", this, 40.0, 1.0, 180.0, 0);
      pitchSpeedMin = new DoubleValue(65, "PitchSpeedMin", this, 40.0, 1.0, 180.0, 0);
      pitchSpeedMax = new DoubleValue(5, "PitchSpeedMax", this, 40.0, 1.0, 180.0, 0);
      minDelay = new DoubleValue(6, "MinDelay", this, 40.0, 0.0, 1000.0, 0);
      maxDelay = new DoubleValue(46445, "MaxDelay", this, 40.0, 0.0, 1000.0, 0);
      gommeFix = new BooleanValue(3284, "GommeFix", this, true);
      grimfunni = new BooleanValue(2356, "Grim Funny", this, true);
      hitChance = new DoubleValue(7, "HitChance", this, 100.0, 0.0, 100.0, 0);
      randomStrength = new DoubleValue(8, "RStrength", this, 0.25, 0.01, 5.0, 2);
      player = new BooleanValue(9, "Player", this, true);
      mob = new BooleanValue(10, "Mob", this, true);
      animal = new BooleanValue(11, "Animal", this, true);
      villager = new BooleanValue(12, "Villager", this, true);
      armorStand = new BooleanValue(13, "ArmorStand", this, true);
      invisible = new BooleanValue(888, "Invisible", this, true);
      interpolation = new BooleanValue(14, "Interpolation", this, false);
      smoothBackRotate = new BooleanValue(15, "SBRotate", this, false);
      stopOnTarget = new BooleanValue(17, "StopOnTarget", this, false);
      perfectHit = new BooleanValue(90, "PerfectHit", this, true);
      perfectHitGomme = new BooleanValue(90, "PHIntave", this, true);
      moveFix = new BooleanValue(18, "MoveFix", this, true);
      silentMoveFix = new BooleanValue(19, "SilentMoveFix", this, true);
      bestHitVec = new BooleanValue(78, "BestHitVec", this, true);
      smartAim = new BooleanValue(20, "SmartAim", this, true);
      throughWalls = new BooleanValue(21, "ThroughWalls", this, false);
      slowDown = new BooleanValue(23, "SlowDown", this, true);
      coolDown = new BooleanValue(1338, "CoolDown", this, true);
      preHit = new BooleanValue(24, "PreHit", this, false);
      inInv = new BooleanValue(43, "InInv", this, false);
      this.player.setVisible(false);
      this.mob.setVisible(false);
      this.animal.setVisible(false);
      this.villager.setVisible(false);
      this.armorStand.setVisible(false);
      this.invisible.setVisible(false);
      randomize = new BooleanValue(44, "Randomize", this, false);
      block = new BooleanValue(45, "Block", this, false);
      multi = new BooleanValue(1337, "Multi", this, false);
      heuristics = new BooleanValue(46, "Heuristics", this, false);
      hazeRange = new BooleanValue(4343905, "HazeRange", this, false);
      hazeAdd = new DoubleValue(4343906, "HazeAdd", this, 0.5, 0.0, 1.0, 2);
      hazeMax = new DoubleValue(4343907, "HazeMax", this, 4.5, 3.0, 6.0, 2);
      intave = new BooleanValue(77, "Intave", this, false);
      advancedRots = new BooleanValue(66, "AdvancedRots", this, true);
      mode = new StringValue(42, "Mode", this, "Advanced", new String[]{"Basic", "Advanced"});
      targets = new BooleansSetting(31, "Targets", this, new Setting[]{this.player, this.mob, this.animal, this.villager, this.armorStand, this.invisible});
      sortMode = new StringValue(26, "TargetSort", this, "Distance", new String[]{"FOV", "Health", "Distance", "Best", "UltimateSwitch"});
      targetRandom = new StringValue(27, "Randomize", this, "Basic", new String[]{"None", "Basic", "Doubled", "OnlyRotation"});
      rangeMode = new StringValue(28, "RangeMode", this, "Legit", new String[]{"Legit", "Normal"});
      attackMode = new StringValue(30, "AttackMode", this, "Legit", new String[]{"Legit", "Pre", "Post"});
      blockMode = new StringValue(29, "BlockMode", this, "Basic", new String[]{"None", "Basic", "Intave", "BlocksMC", "Gomme1vs1", "Verus", "UpdatedNCP", "Custom"});
      startBlock = new DoubleValue(38, "StartBlock", this, 9.0, 0.0, 10.0, 0);
      endBlock = new DoubleValue(39, "EndBlock", this, 2.0, 0.0, 10.0, 0);
      endBlockHitOnly = new DoubleValue(39, "Unblock", this, 8.0, 0.0, 10.0, 0);
      unblockHit = new BooleanValue(40, "UnblockHit", this, true);
      unblockHitOnly = new BooleanValue(41, "UnblockHitOnly", this, false);
      onWorld = new BooleanValue(123, "DisableOnWorld", this, false);
   }

   public BooleanValue onWorld;
   @EventTarget
   public void onWorld(EventWorld eventWorld) {
      if(onWorld.getBoolean()) {
         setToggled(false);
      }
   }

   @Override
   public void onEnable() {
      this.rotationUtil = new RotationUtil();
      if (mc.thePlayer != null) {
         this.rots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
         this.lastRots = new float[]{mc.thePlayer.prevRotationYaw, mc.thePlayer.prevRotationPitch};
      }

      this.range = this.rangeSetting.getValue();
      target = null;
      this.positions.clear();
      this.time = 0L;

      try {
         FileManager<Integer> fileManager = new FileManager<>();
         ArrayList<Integer> list = fileManager.readFileAll("xenzarecode/clickpattern", "ClickingPattern.json");
         this.recordedClicks = list.toArray();
      } catch (Exception var3) {
      }
   }

   @Override
   public void onDisable() {
      if(blockMode.getSelected().equals("BlocksMC") || blockMode.getSelected().equals("Intave")) {
         //mc.thePlayer.sendQueue.addToSendQueueDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
         mc.thePlayer.stopUsingItem();
      }
      ItemRenderer.fakeBlocking = false;
      if (mc.thePlayer.inventory.getCurrentItem() != null
         && mc.thePlayer.inventory.getCurrentItem().getItem() != null
         && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), Keyboard.isKeyDown(1));
      }

      this.positions.clear();
      target = null;
   }

   @EventTarget
   public void onEventClickGui(EventClickGui eventClickGui) {
      this.range = this.rangeSetting.getValue();
   }

   @EventTarget
   public void onEventAttackSlowDown(EventAttackSlowdown attackSlowdown) {
      if (!this.slowDown.getBoolean()) {
         attackSlowdown.setSprint(true);
         attackSlowdown.setSlowDown(1.0);
      }
   }

   @EventTarget
   public void onEventEarlyTick(EventEarlyTick eventTick) {
      if (mc.theWorld != null) {
         this.setDisplayName(super.getName() + " §8" + 0);
      }

      if (mc.currentScreen == null && this.mode.getSelected().equals("Advanced")) {
         Object[] listOfTargets = null;
         if (mc.theWorld != null) {
            String lastBest = this.sortMode.getSelected();
            switch(lastBest) {
               case "FOV":
                  listOfTargets = mc.theWorld.loadedEntityList.stream().filter(this::canAttacked).sorted(Comparator.comparingDouble(this::fov)).toArray();
                  break;
               case "Health":
                  listOfTargets = mc.theWorld
                     .loadedEntityList
                     .stream()
                     .filter(this::canAttacked)
                     .sorted(Comparator.comparingDouble(entityx -> (double)((EntityLivingBase)entityx).getHealth()))
                     .toArray();
                  break;
               case "Distance":
                  listOfTargets = mc.theWorld
                     .loadedEntityList
                     .stream()
                     .filter(this::canAttacked)
                     .sorted(Comparator.comparingDouble(entityx -> (double)mc.thePlayer.getDistanceToEntity(entityx)))
                     .toArray();
                  break;
               case "Best":
                  listOfTargets = mc.theWorld
                     .loadedEntityList
                     .stream()
                     .filter(this::canAttacked)
                     .sorted(Comparator.comparingDouble(this::isBestTarget))
                     .toArray();
                  break;
               case "UltimateSwitch":
                  listOfTargets = mc.theWorld
                     .loadedEntityList
                     .stream()
                     .filter(this::canAttacked)
                     .sorted(Comparator.comparingDouble(this::ultimateTarget))
                     .toArray();
            }

            if (mc.theWorld != null && listOfTargets != null) {
               this.setDisplayName(super.getName() + " §8" + Math.min(listOfTargets.length, 9));
            }
         }

         if (!this.canAttacked(target)) {
            target = null;
         }

         Vec3 lastBest = this.best;
         EntityLivingBase last = target;

         assert listOfTargets != null;

         if (listOfTargets.length > 0) {
            if (this.smartAim.getBoolean() && !this.throughWalls.getBoolean()) {
               boolean b = false;
               EntityLivingBase entity = (EntityLivingBase)listOfTargets[0];
               double ex = entity.posX;
               double ey = entity.posY + (double)entity.getEyeHeight();
               double ez = entity.posZ;
               if (this.bestHitVec.getBoolean()) {
                  Vec3 entityVec = RotationUtil.getBestHitVec(entity);
                  ex = entityVec.xCoord;
                  ey = entityVec.yCoord;
                  ez = entityVec.zCoord;
               }

               if (mc.thePlayer.canPosBeSeen(new Vec3(ex, ey, ez))) {
                  this.best = new Vec3(ex, ey, ez);
                  target = (EntityLivingBase)listOfTargets[0];
                  b = true;
               } else {
                  for(int i = 0; i < listOfTargets.length; ++i) {
                     AxisAlignedBB boundingBox = ((EntityLivingBase)listOfTargets[i]).getEntityBoundingBox();
                     this.best = null;
                     double nearest = 15.0;

                     for(double x = boundingBox.minX; x <= boundingBox.maxX; x += 0.07) {
                        for(double z = boundingBox.minZ; z <= boundingBox.maxZ; z += 0.07) {
                           for(double y = boundingBox.minY; y <= boundingBox.maxY; y += 0.07) {
                              Vec3 pos = new Vec3(x, y, z);
                              if (mc.thePlayer.canPosBeSeen(pos)) {
                                 Vec3 eyes = mc.thePlayer.getPositionEyes(1.0F);
                                 double dist = Math.sqrt(Math.pow(x - eyes.xCoord, 2.0) + Math.pow(y - eyes.yCoord, 2.0) + Math.pow(z - eyes.zCoord, 2.0));
                                 if (dist <= nearest) {
                                    nearest = dist;
                                    this.best = pos;
                                 }
                              }
                           }
                        }
                     }

                     if (this.best != null) {
                        target = (EntityLivingBase)listOfTargets[i];
                        b = true;
                        break;
                     }
                  }
               }

               if (!b) {
                  target = null;
               }
            } else {
               target = (EntityLivingBase)listOfTargets[0];
            }
         }

         if (last != null && target != null && !target.getName().equals(last.getName()) && this.targetDelay.getValue() != 0.0) {
            if (!this.targetDelayTimer.reached((long)this.targetDelay.getValue() + RandomUtil.nextLong(0L, 60L))) {
               target = last;
               this.best = lastBest;
            } else {
               this.targetDelayTimer.reset();
            }
         }

         if (this.mode.getSelected().equals("Advanced")) {
            this.rotateNormal();
         }
      }
   }

   private void rotateNormal() {
      SecureRandom secureRandom = new SecureRandom();
      float deltaYaw = RandomUtil.nextFloat(this.yawSpeedMin.getValue() - 0.001F, this.yawSpeedMax.getValue()) / 2.0F
         + secureRandom.nextFloat()
         + RandomUtil.nextFloat(this.yawSpeedMin.getValue() - 0.001F, this.yawSpeedMax.getValue()) / 2.0F;
      float deltaPitch = RandomUtil.nextFloat(this.pitchSpeedMin.getValue() - 0.001F, this.pitchSpeedMax.getValue()) / 2.0F
         + secureRandom.nextFloat()
         + RandomUtil.nextFloat(this.pitchSpeedMin.getValue() - 0.001F, this.pitchSpeedMax.getValue()) / 2.0F;
      if (target != null) {
         this.backRotate = true;
         double distance = mc.thePlayer.getDistanceToEntity(target);
         if (distance < 0.4) {
            deltaPitch = RandomUtil.nextFloat(0.0F, 4.0F) / 2.0F + RandomUtil.nextFloat(0.0F, 4.0F) / 2.0F;
            deltaYaw = RandomUtil.nextFloat(0.0F, 4.0F) / 2.0F + RandomUtil.nextFloat(0.0F, 4.0F) / 2.0F;
         }

         float[] floats = this.rotationUtil
            .faceEntityCustom(
               target,
               deltaYaw,
               deltaPitch,
               this.rots[0],
               this.rots[1],
               this.targetRandom.getSelected(),
               this.interpolation.getBoolean(),
               this.smartAim.getBoolean(),
               this.stopOnTarget.getBoolean(),
               (float)this.randomStrength.getValue(),
               this.best,
               this.throughWalls.getBoolean(),
               this.advancedRots.getBoolean(),
               this.heuristics.getBoolean(),
               this.intave.getBoolean(),
               this.bestHitVec.getBoolean()
            );
         if (floats == null) {
            this.rots = this.lastRots;
            this.setRotation();
            this.lastRots = this.rots;
            target = null;
            return;
         }

         this.lastRots = this.rots;
         this.rots = floats;
         this.setRotation();
      } else if (this.smoothBackRotate.getBoolean()) {
         if (this.backRotate) {
            if (this.rots[0] % 360.0F <= Augustus.getInstance().getYawPitchHelper().realYaw % 360.0F + 20.0F
               && this.rots[0] % 360.0F > Augustus.getInstance().getYawPitchHelper().realYaw % 360.0F - 20.0F
               && this.rots[1] % 360.0F <= Augustus.getInstance().getYawPitchHelper().realPitch % 360.0F + 20.0F
               && this.rots[1] % 360.0F > Augustus.getInstance().getYawPitchHelper().realPitch % 360.0F - 20.0F) {
               this.backRotate = false;
               this.resetRotation();
            } else {
               float[] f = this.rotationUtil
                  .backRotate(
                     deltaYaw,
                     deltaPitch,
                     this.rots[0],
                     this.rots[1],
                     Augustus.getInstance().getYawPitchHelper().realYaw,
                     Augustus.getInstance().getYawPitchHelper().realPitch
                  );
               this.lastRots = this.rots;
               this.rots = f;
               this.setRotation();
            }
         } else {
            this.resetRotation();
         }
      } else {
         this.resetRotation();
      }
   }

   private void basicMode() {
      if (this.mode.getSelected().equals("Basic")) {
         Object[] listOfTargets = null;
         if (mc.theWorld != null) {
            String var2 = this.sortMode.getSelected();
            switch(var2) {
               case "FOV":
                  listOfTargets = mc.theWorld.loadedEntityList.stream().filter(this::canAttacked).sorted(Comparator.comparingDouble(this::fov)).toArray();
                  break;
               case "Health":
                  listOfTargets = mc.theWorld
                     .loadedEntityList
                     .stream()
                     .filter(this::canAttacked)
                     .sorted(Comparator.comparingDouble(entity -> (double)((EntityLivingBase)entity).getHealth()))
                     .toArray();
                  break;
               case "Distance":
                  listOfTargets = mc.theWorld
                     .loadedEntityList
                     .stream()
                     .filter(this::canAttacked)
                     .sorted(Comparator.comparingDouble(entity -> (double)mc.thePlayer.getDistanceToEntity(entity)))
                     .toArray();
                  break;
               case "Best":
                  listOfTargets = mc.theWorld
                     .loadedEntityList
                     .stream()
                     .filter(this::canAttacked)
                     .sorted(Comparator.comparingDouble(this::isBestTarget))
                     .toArray();
                  break;
               case "UltimateSwitch":
                  listOfTargets = mc.theWorld
                     .loadedEntityList
                     .stream()
                     .filter(this::canAttacked)
                     .sorted(Comparator.comparingDouble(this::ultimateTarget))
                     .toArray();
            }

            if (listOfTargets != null) {
               this.setDisplayName(super.getName() + " §8" + Math.min(listOfTargets.length, 9));
            }
         }

         if (!this.canAttacked(target)) {
            target = null;
         }

         assert listOfTargets != null;

         if (listOfTargets.length > 0) {
            target = (EntityLivingBase)listOfTargets[0];
         }

         if (target == null) {
            this.lastTarget = target;
         } else {
            this.rots = this.rotationUtil.basicRotation(target, this.rots[0], this.rots[1], this.randomize.getBoolean());
         }
      }
   }

   @EventTarget
   public void onEventClick(EventClick eventClick) {
      if (target != null && !this.isScaffoldToggled() && mc.currentScreen == null) {
         eventClick.setCanceled(true);
      }

      if (this.attackMode.getSelected().equalsIgnoreCase("Legit") && this.mode.getSelected().equals("Advanced")) {
         this.attack();
      }

      if(blockMode.getSelected().equals("BlocksMC")) {
         mc.thePlayer.sendQueue.addToSendQueueDirect(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
      }

      if (this.hazeRange.getBoolean()) {
         if (target != null && target.hurtTime == 10) {
            this.range = Math.min(this.range + this.hazeAdd.getValue(), this.hazeMax.getValue());
            this.lastTime = System.currentTimeMillis();
         }

         if (target != this.lastTarget) {
            this.range = this.rangeSetting.getValue();
         }

         if (Math.abs(System.currentTimeMillis() - this.lastTime) > 1000L) {
            this.range = this.rangeSetting.getValue();
         }
      } else {
         this.range = this.rangeSetting.getValue();
      }
   }

   @EventTarget
   public void onEventPostMotion(EventPostMotion eventPostMotion) {
      if (this.attackMode.getSelected().equalsIgnoreCase("Post") && this.mode.getSelected().equals("Advanced")) {
         this.attack();
      }

      if (this.mode.getSelected().equals("Basic") && !this.isScaffoldToggled()) {
         if (!this.inInv.getBoolean() && mc.currentScreen != null) {
            return;
         }

         if (target == null) {
            return;
         }

         if (this.block.getBoolean()) {
            this.block();
         }
      }

      if (this.mode.getSelected().equals("Advanced")
         && !this.isScaffoldToggled()
         && target != null
         && mc.thePlayer.inventory.getCurrentItem() != null
         && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword
         && (this.blockMode.getSelected().equals("Basic") || this.blockMode.getSelected().equals("UpdatedNCP") || this.blockMode.getSelected().equals("Gomme1vs1"))) {
         this.block();
      }
   }

   @EventTarget
   public void onEventPreMotion(EventPreMotion eventPreMotion) {
      if (target != null && !this.isScaffoldToggled()) {
         String eventAttackEntity = this.blockMode.getSelected();
         switch(eventAttackEntity) {
            case "BlocksMC": {
               ItemRenderer.fakeBlocking = true;
               break;
            }
            case "Intave":
               ItemRenderer.fakeBlocking = true;
               if (mc.thePlayer.getHeldItem() != null && (target != null || this.ssBlocking)) {
                  if (this.ssBlocking) {
                     mc.thePlayer
                        .sendQueue
                        .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, mc.thePlayer.getPosition(), EnumFacing.DOWN));
                     this.ssBlocking = false;
                  } else {
                     mc.rightClickMouse();
                     this.ssBlocking = true;
                  }
               }
         }
      }

      if (this.attackMode.getSelected().equalsIgnoreCase("Pre") && this.mode.getSelected().equals("Advanced")) {
         this.attack();
      }

      if (this.mode.getSelected().equals("Basic")) {
         if (!this.inInv.getBoolean() && mc.currentScreen != null) {
            return;
         }

         this.basicMode();
         if (target == null) {
            this.resetRotation();
            return;
         }

         eventPreMotion.setYaw(this.rots[0]);
         eventPreMotion.setPitch(this.rots[1]);
         mc.thePlayer.rotationYawHead = this.rots[0];
         mc.thePlayer.rotationPitchHead = this.rots[1];
         mc.thePlayer.renderYawOffset = this.rots[0];
         this.lastRots = this.rots;
         if (this.hitTimeHelper.reached(this.randomDelay)) {
            if (this.block.getBoolean()) {
               this.unBlock();
            }

            mc.thePlayer.swingItem();
            EventAttackEntity eventAttackEntity = new EventAttackEntity();
            if (this.multi.getBoolean()) {
               Object[] listOfTargets = mc.theWorld
                  .loadedEntityList
                  .stream()
                  .filter(this::canAttacked)
                  .sorted(Comparator.comparingDouble(this::ultimateTarget))
                  .toArray();

               for(Object object : listOfTargets) {
                  if (object instanceof Entity) {
                     Entity entity = (Entity)object;
                     if ((double)mc.thePlayer.getDistanceToEntity(entity) <= this.range) {
                        EventClickKillAura eventClickKillAura = new EventClickKillAura();
                        EventHandler.call(eventClickKillAura);
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                     }
                  }
               }
            } else if (this.slowDown.getBoolean()) {
               if ((double)mc.thePlayer.getDistanceToEntity(target) <= this.range) {
                  mc.playerController.attackEntity(mc.thePlayer, target);
                  EventHandler.call(eventAttackEntity);
               }
            } else if ((double)mc.thePlayer.getDistanceToEntity(target) <= this.range) {
               EventClickKillAura eventClickKillAura = new EventClickKillAura();
               EventHandler.call(eventClickKillAura);
               mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
               EventHandler.call(eventAttackEntity);
            }

            this.setRandomDelay();
            this.hitTimeHelper.reset();
         }
      }
   }

   @EventTarget
   public void onEventMove(EventMove eventMove) {
      if (mc.currentScreen == null
         && target != null
         && this.mode.getSelected().equals("Advanced")
         && !this.isScaffoldToggled()
         && !mm.targetStrafe.isToggled()
         && !this.moveFix.getBoolean()) {
         eventMove.setYaw(Augustus.getInstance().getYawPitchHelper().realYaw);
      }
   }

   @EventTarget
   public void onEventJump(EventJump eventJump) {
      if (mc.currentScreen == null
         && target != null
         && this.mode.getSelected().equals("Advanced")
         && !this.isScaffoldToggled()
         && !mm.targetStrafe.isToggled()
         && !this.moveFix.getBoolean()) {
         eventJump.setYaw(Augustus.getInstance().getYawPitchHelper().realYaw);
      }
   }

   @EventTarget
   public void onEventSilentMove(EventSilentMove eventSilentMove) {
      if (mc.currentScreen == null
         && (target != null || this.smoothBackRotate.getBoolean() && this.backRotate)
         && this.mode.getSelected().equals("Advanced")
         && !this.isScaffoldToggled()
         && !mm.targetStrafe.isToggled()
         && this.moveFix.getBoolean()
         && this.silentMoveFix.getBoolean()) {
         eventSilentMove.setSilent(true);
      }
   }

   private boolean shouldHit() {
      if (this.perfectHit.getBoolean()) {
         if (this.perfectHitGomme.getBoolean()) {
            MovingObjectPosition objectPosition = RayTraceUtil.rayCast(2.0F, this.rots);
            if (objectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
               && mc.objectMouseOver.entityHit instanceof EntityLivingBase
               && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
               return false;
            }
         }

         if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase)mc.objectMouseOver.entityHit;
            if (entity.hurtTime == 0 || entity.hurtTime == 1) {
               return true;
            }
         }

         if (this.gommeFix.getBoolean() && target.hurtTime == 4) {
            return false;
         }
      }

      return this.hitTimeHelper.reached(this.randomDelay) || this.hittedTimeHelper.reached(1000L);
   }

   private void attack() {
      ItemRenderer.fakeBlocking = false;
      if (mc.currentScreen == null) {
         if (target != null && !this.isScaffoldToggled()) {
            if (this.shouldHit()) {
               label151:
               if (mc.thePlayer.inventory.getCurrentItem() != null
                  && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword
                  && !this.isScaffoldToggled()) {
                  String var1 = this.blockMode.getSelected();
                  switch(var1) {
                     case "UpdatedNCP": {
                        if(BadPacket.bad(false, true, true, true, true)) {
                           return;
                        }
                        break;
                     }
                     case "Basic":
                        this.unBlock();
                        this.rawHit();
                        this.hitTimeHelper.reset();
                        break label151;
                     case "Verus":
                        this.rawHit();
                        this.hitTimeHelper.reset();
                        if (mc.thePlayer.isSwingInProgress
                           && mc.thePlayer.hurtTime != 0
                           && mc.thePlayer.getHeldItem() != null
                           && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                           KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        }
                        break label151;
                     case "Custom":
                        if (this.unblockHit.getBoolean()) {
                           if (this.unblockHitOnly.getBoolean()) {
                              if ((double) target.hurtTime <= this.endBlockHitOnly.getValue()) {
                                 this.unBlock();
                                 this.rawHit();
                                 mc.rightClickMouse();
                                 KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                              }
                           } else if ((double) target.hurtTime <= this.endBlock.getValue()) {
                              if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                 KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                                 if ((double) target.hurtTime < this.endBlock.getValue()) {
                                    this.rawHit();
                                 }
                              } else {
                                 KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                              }
                           } else if ((double) target.hurtTime == this.startBlock.getValue()) {
                              mc.rightClickMouse();
                              KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                           }
                        } else {
                           KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                           this.rawHit();
                        }
                        break label151;
                  }

                  if (mc.thePlayer.isUsingItem()) {
                     if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                        mc.playerController.onStoppedUsingItem(mc.thePlayer);
                     }

                     while(mc.gameSettings.keyBindAttack.isPressed()) {
                     }

                     while(mc.gameSettings.keyBindUseItem.isPressed()) {
                     }

                     while(mc.gameSettings.keyBindPickBlock.isPressed()) {
                     }
                  } else {
                     while(mc.gameSettings.keyBindAttack.isPressed()) {
                     }

                     this.rawHit();

                     while(mc.gameSettings.keyBindUseItem.isPressed()) {
                        mc.rightClickMouse();
                     }

                     while(mc.gameSettings.keyBindPickBlock.isPressed()) {
                        mc.middleClickMouse();
                     }
                  }

                  if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.getRightClickDelayTimer() == 0 && !mc.thePlayer.isUsingItem()) {
                     mc.rightClickMouse();
                  }
               } else {
                  ItemRenderer.fakeBlocking = false;
                  if (mc.thePlayer.isUsingItem()) {
                     if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                        mc.playerController.onStoppedUsingItem(mc.thePlayer);
                     }

                     while(mc.gameSettings.keyBindAttack.isPressed()) {
                     }

                     while(mc.gameSettings.keyBindUseItem.isPressed()) {
                     }

                     while(mc.gameSettings.keyBindPickBlock.isPressed()) {
                     }
                  } else {
                     while(mc.gameSettings.keyBindAttack.isPressed()) {
                     }

                     this.rawHit();

                     while(mc.gameSettings.keyBindUseItem.isPressed()) {
                        mc.rightClickMouse();
                     }

                     while(mc.gameSettings.keyBindPickBlock.isPressed()) {
                        mc.middleClickMouse();
                     }
                  }

                  if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.getRightClickDelayTimer() == 0 && !mc.thePlayer.isUsingItem()) {
                     mc.rightClickMouse();
                  }
               }

               this.setRandomDelay();
               this.hitTimeHelper.reset();
            } else {
               this.time = 0L;
            }
         } else if (mc.thePlayer.inventory.getCurrentItem() != null
            && mc.thePlayer.inventory.getCurrentItem().getItem() != null
            && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword
            && !this.isScaffoldToggled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), Mouse.isButtonDown(1));
         }

         mc.sendClickBlockToController(false);
      }
   }

   private void rawHit() {
      if(!this.grimfunni.getBoolean()) {
         if (this.hitChance((int) this.hitChance.getValue())) {
            mm.timerRange.attack();
            if (!this.rangeMode.getSelected().equals("Normal") || (double) mc.thePlayer.getDistanceToEntity(target) < this.range) {
               if (this.preHit.getBoolean() || mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                  if (mc.objectMouseOver == null
                          || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY
                          || !(mc.objectMouseOver.entityHit instanceof EntityPlayer)
                          || (!mm.teams.isToggled() || !mm.teams.getTeammates().contains(mc.objectMouseOver.entityHit))
                          && (!mm.midClick.isToggled() || !MidClick.friends.contains(mc.objectMouseOver.entityHit.getName()) || mm.midClick.noFiends)) {
                     if (this.coolDown.getBoolean()) {
                        int tickCounter = 1;
                        if (mc.thePlayer.getCurrentEquippedItem() != null
                                && (
                                mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword
                                        || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemTool
                        )) {
                           tickCounter = mc.thePlayer.getCurrentEquippedItem().getItem().coolDownTicks;
                           if (mc.thePlayer.ticksSinceLastSwing >= tickCounter) {
                              mc.clickMouse();
                           }
                        } else {
                           mc.clickMouse();
                        }
                     } else {
                        mc.clickMouse();
                     }

                     this.hittedTimeHelper.reset();
                     if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                        this.lastTarget = target;
                     }
                  }
               } else {
                  this.lastTarget = target;
               }
            }
         }
      } else {
//         String client = "§6[§9" + Augustus.getInstance().getName() + "§6] ";
         for(int i = 0; i < 3; i++) {
//            mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(client + "§7uwu debug"));
            if (this.hitChance((int) this.hitChance.getValue())) {
               mm.timerRange.attack();
               if (!this.rangeMode.getSelected().equals("Normal") || (double) mc.thePlayer.getDistanceToEntity(target) < this.range) {
                  if (this.preHit.getBoolean() || mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                     if (mc.objectMouseOver == null
                             || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY
                             || !(mc.objectMouseOver.entityHit instanceof EntityPlayer)
                             || (!mm.teams.isToggled() || !mm.teams.getTeammates().contains(mc.objectMouseOver.entityHit))
                             && (!mm.midClick.isToggled() || !MidClick.friends.contains(mc.objectMouseOver.entityHit.getName()) || mm.midClick.noFiends)) {
                        if (this.coolDown.getBoolean()) {
                           int tickCounter = 1;
                           if (mc.thePlayer.getCurrentEquippedItem() != null
                                   && (
                                   mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword
                                           || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemTool
                           )) {
                              tickCounter = mc.thePlayer.getCurrentEquippedItem().getItem().coolDownTicks;
                              if (mc.thePlayer.ticksSinceLastSwing >= tickCounter) {
                                 mc.clickMouse();
                              }
                           } else {
                              mc.clickMouse();
                           }
                        } else {
                           mc.clickMouse();
                        }

                        this.hittedTimeHelper.reset();
                        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                           this.lastTarget = target;
                        }
                     }
                  } else {
                     this.lastTarget = target;
                  }
               }
            }
         }
      }
   }

   private void block() {
      if (mc.thePlayer != null
         && mc.thePlayer.inventory != null
         && mc.thePlayer.inventory.getCurrentItem() != null
         && mc.thePlayer.inventory.getCurrentItem().getItem() != null
         && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
         mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
      }
   }

   private void unBlock() {
      mc.playerController.onStoppedUsingItem(mc.thePlayer);
   }

   private void setRandomDelay() {
      if (this.minDelay.getValue() == 0.0 && this.maxDelay.getValue() == 0.0) {
         this.randomDelay = 0L;
      } else if (Math.abs(this.minDelay.getValue() - this.maxDelay.getValue()) > 0.0) {
         this.randomDelay = (long)RandomUtil.nextSecureInt((int)this.minDelay.getValue(), (int)this.maxDelay.getValue());
      } else {
         this.randomDelay = (long)this.minDelay.getValue();
      }
   }

   private boolean hitChance(int hitChance) {
      int randomNumber = ThreadLocalRandom.current().nextInt(0, 99);
      return randomNumber <= hitChance;
   }

   private void setRotation() {
      if (!this.isScaffoldToggled()) {
         mc.thePlayer.rotationYaw = this.rots[0];
         mc.thePlayer.rotationPitch = this.rots[1];
         mc.thePlayer.prevRotationYaw = this.lastRots[0];
         mc.thePlayer.prevRotationPitch = this.lastRots[1];
      } else {
         this.resetRotation();
      }
   }

   private void resetRotation() {
      this.rots = this.lastRots;
      this.rots[0] = Augustus.getInstance().getYawPitchHelper().realYaw;
      this.rots[1] = Augustus.getInstance().getYawPitchHelper().realPitch;
      this.lastRots[0] = Augustus.getInstance().getYawPitchHelper().realLastYaw;
      this.lastRots[1] = Augustus.getInstance().getYawPitchHelper().realLastPitch;
   }

   private boolean canAttacked(Entity entity) {
      if (entity instanceof EntityLivingBase) {
         if (entity.isInvisible() && !this.invisible.getBoolean()) {
            return false;
         }

         if (entity instanceof EntitySlime) {
            return false;
         }

         if (((EntityLivingBase)entity).deathTime > 1) {
            return false;
         }

         if (entity instanceof EntityArmorStand && !this.armorStand.getBoolean()) {
            return false;
         }

         if (entity instanceof EntityAnimal && !this.animal.getBoolean()) {
            return false;
         }

         if (entity instanceof EntityMob && !this.mob.getBoolean()) {
            return false;
         }

         if (entity instanceof EntityPlayer && !this.player.getBoolean()) {
            return false;
         }

         if (entity instanceof EntityVillager && !this.villager.getBoolean()) {
            return false;
         }

         if (entity.ticksExisted < 1) {
            return false;
         }

         if (entity instanceof EntityPlayer && mm.teams.isToggled() && mm.teams.getTeammates().contains(entity)) {
            return false;
         }

         if (entity instanceof EntityPlayer && (entity.getName().equals("§aShop") || entity.getName().equals("SHOP") || entity.getName().equals("UPGRADES"))) {
            return false;
         }

         if (this.mode.getSelected().equals("Advanced")) {
            if (!mc.thePlayer.canEntityBeSeen(entity) && !this.throughWalls.getBoolean() && !this.smartAim.getBoolean()) {
               return false;
            }
         } else if (!mc.thePlayer.canEntityBeSeen(entity) && !this.throughWalls.getBoolean()) {
            return false;
         }

         if (entity.isDead) {
            return false;
         }

         if (entity instanceof EntityPlayer && mm.antiBot.isToggled() && AntiBot.bots.contains(entity)) {
            return false;
         }

         if (entity instanceof EntityPlayer && !mm.midClick.noFiends && MidClick.friends.contains(entity.getName())) {
            return false;
         }
      }

      return entity instanceof EntityLivingBase && entity != mc.thePlayer && (double)mc.thePlayer.getDistanceToEntity(entity) < this.preRange.getValue();
   }

   private double isBestTarget(Entity entity) {
      if (entity instanceof EntityLivingBase) {
         double distance = mc.thePlayer.getDistanceToEntity(entity);
         double health = ((EntityLivingBase)entity).getHealth();
         double hurtTime = 10.0;
         if (entity instanceof EntityPlayer) {
            hurtTime = ((EntityPlayer)entity).hurtTime;
         }

         return distance * 2.0 + health + hurtTime * 4.0;
      } else {
         return 1000.0;
      }
   }

   private double ultimateTarget(Entity entity) {
      if (!(entity instanceof EntityLivingBase)
         || (!RayTraceUtil.couldHit(entity, 1.0F, this.rots[0], this.rots[1], 180.0F, 180.0F) || this.range != 3.0) && this.range == 3.0) {
         return 1000.0;
      } else {
         double distance = mc.thePlayer.getDistanceToEntity(entity);
         double hurtTime = ((EntityLivingBase)entity).hurtTime * 6;
         return hurtTime + distance;
      }
   }

   private double fov(Entity entity) {
      if (entity instanceof EntityLivingBase) {
         float yaw = RotationUtil.getFovToTarget(
            entity.posX, entity.posY, entity.posZ, Augustus.getInstance().getYawPitchHelper().realYaw, Augustus.getInstance().getYawPitchHelper().realPitch
         )[0];
         return Math.abs(yaw);
      } else {
         return 1000.0;
      }
   }
   @EventTarget
   public void onEvent3D(EventRender3D eventRender3D) {
      if (mm.line.isToggled() && mm.line.killAura.getBoolean()) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(2848);
         GL11.glDisable(3553);
         GlStateManager.disableCull();
         GL11.glDepthMask(false);
         Vec3 vec31 = mc.thePlayer.getCustomLook(1.0F, this.rots[0], this.rots[1]);
         Vec3 vec32 = mc.thePlayer.getPositionEyes(1.0F).addVector(vec31.xCoord * 3.0, vec31.yCoord * 3.0, vec31.zCoord * 3.0);
         float x = (float)vec32.xCoord;
         float y = (float)vec32.yCoord;
         float z = (float)vec32.zCoord;
         this.positions.add(new double[]{(double)x, (double)y, (double)z, (double)System.currentTimeMillis()});
         this.positions.removeIf(values -> this.shouldRenderPoint(values[3]));
         GL11.glColor4f(
            (float)mm.line.killAuraColor.getColor().getRed() / 255.0F,
            (float)mm.line.killAuraColor.getColor().getGreen() / 255.0F,
            (float)mm.line.killAuraColor.getColor().getBlue() / 255.0F,
            (float)mm.line.killAuraColor.getColor().getAlpha() / 255.0F
         );
         GL11.glLineWidth((float)mm.line.killAuraLineWidth.getValue());
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         worldrenderer.begin(3, DefaultVertexFormats.POSITION);
         double[] lastPosition = new double[]{-2.0, -1.0, -1.0};

         for(double[] position : this.positions) {
            if (!Arrays.equals(lastPosition, new double[]{-2.0, -1.0, -1.0}) && !Arrays.equals(lastPosition, position)) {
               worldrenderer.pos(
                     (double)((float)position[0]) - mc.getRenderManager().getRenderPosX(),
                     (double)((float)position[1]) - mc.getRenderManager().getRenderPosY(),
                     (double)((float)position[2]) - mc.getRenderManager().getRenderPosZ()
                  )
                  .endVertex();
            }

            lastPosition = position;
         }

         tessellator.draw();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDepthMask(true);
         GlStateManager.enableCull();
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(2848);
      }
   }

   private boolean shouldRenderPoint(double time) {
      return Math.abs(time - (double)System.currentTimeMillis()) > mm.line.killAuraLineTime.getValue();
   }

   private boolean isScaffoldToggled() {
      return /*mm.scaffoldWalk.isToggled() || mm.newScaffold.isToggled() || */mm.blockFly.isToggled();
   }
   public Entity findTarget(boolean allowSame, double range) {
      ArrayList<Entity> entities = new ArrayList<>();
      for(Entity entity : mc.theWorld.loadedEntityList) {
         if(entity instanceof EntityLivingBase && entity != mc.thePlayer) {
            if(canAttacked(entity, range)) {
               entities.add(entity);
            }
         }
      }

      if(entities.size() > 0) {
         switch(sortMode.getSelected()) {
            case "FOV":
               entities = mc.theWorld.loadedEntityList.stream().filter(this::canAttacked).sorted(Comparator.comparingDouble(this::fov)).collect(Collectors.toCollection(ArrayList::new));
               break;
            case "Health":
               entities = mc.theWorld
                       .loadedEntityList
                       .stream()
                       .filter(this::canAttacked)
                       .sorted(Comparator.comparingDouble(entityx -> (double)((EntityLivingBase)entityx).getHealth()))
                       .collect(Collectors.toCollection(ArrayList::new));
               break;
            case "Distance":
               entities = mc.theWorld
                       .loadedEntityList
                       .stream()
                       .filter(this::canAttacked)
                       .sorted(Comparator.comparingDouble(entityx -> (double)mc.thePlayer.getDistanceToEntity(entityx)))
                       .collect(Collectors.toCollection(ArrayList::new));
               break;
            case "Best":
               entities = mc.theWorld
                       .loadedEntityList
                       .stream()
                       .filter(this::canAttacked)
                       .sorted(Comparator.comparingDouble(this::isBestTarget))
                       .collect(Collectors.toCollection(ArrayList::new));
               break;
            case "UltimateSwitch":
               entities = mc.theWorld
                       .loadedEntityList
                       .stream()
                       .filter(this::canAttacked)
                       .sorted(Comparator.comparingDouble(this::ultimateTarget))
                       .collect(Collectors.toCollection(ArrayList::new));
         }
         try {
            if (!allowSame && entities.size() > 1 && entities.get(0) == target) {
               return entities.get(1);
            } else {
               return entities.get(0);
            }
         } catch (IndexOutOfBoundsException e) {
            return null;
         }
      }

      return null;
   }

   private boolean canAttacked(Entity entity, double range) {
      if (entity instanceof EntityLivingBase) {
         if (entity.isInvisible() && !this.invisible.getBoolean()) {
            return false;
         }

         if (entity instanceof EntitySlime) {
            return false;
         }

         if (((EntityLivingBase)entity).deathTime > 1) {
            return false;
         }

         if (entity instanceof EntityArmorStand && !this.armorStand.getBoolean()) {
            return false;
         }

         if (entity instanceof EntityAnimal && !this.animal.getBoolean()) {
            return false;
         }

         if (entity instanceof EntityMob && !this.mob.getBoolean()) {
            return false;
         }

         if (entity instanceof EntityPlayer && !this.player.getBoolean()) {
            return false;
         }

         if (entity instanceof EntityVillager && !this.villager.getBoolean()) {
            return false;
         }

         if (entity.ticksExisted < 1) {
            return false;
         }

         if (entity instanceof EntityPlayer && mm.teams.isToggled() && mm.teams.getTeammates().contains(entity)) {
            return false;
         }

         if (entity instanceof EntityPlayer && (entity.getName().equals("§aShop") || entity.getName().equals("SHOP") || entity.getName().equals("UPGRADES"))) {
            return false;
         }

         if (this.mode.getSelected().equals("Advanced")) {
            if (!mc.thePlayer.canEntityBeSeen(entity) && !this.throughWalls.getBoolean() && !this.smartAim.getBoolean()) {
               return false;
            }
         } else if (!mc.thePlayer.canEntityBeSeen(entity) && !this.throughWalls.getBoolean()) {
            return false;
         }

         if (entity.isDead) {
            return false;
         }

         if (entity instanceof EntityPlayer && mm.antiBot.isToggled() && AntiBot.bots.contains(entity)) {
            return false;
         }

         if (entity instanceof EntityPlayer && !mm.midClick.noFiends && MidClick.friends.contains(entity.getName())) {
            return false;
         }
      }

      return entity instanceof EntityLivingBase && entity != mc.thePlayer && (double)mc.thePlayer.getDistanceToEntity(entity) < range;
   }

}
