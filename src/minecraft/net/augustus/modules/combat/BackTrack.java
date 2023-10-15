package net.augustus.modules.combat;

import net.augustus.events.EventEarlyTick;
import net.augustus.events.EventReadPacket;
import net.augustus.events.EventRender3D;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.misc.MidClick;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.BooleansSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.Setting;
import net.augustus.utils.RenderUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class BackTrack extends Module {
   private final TimeHelper timeHelper = new TimeHelper();

   private final ArrayList<Packet> packets = new ArrayList<>();

   public AxisAlignedBB boundingBox;

   public boolean b;

   public boolean bb;

   public boolean aBoolean;

   public DoubleValue hitRange = new DoubleValue(6, "MaxHitRange", this, 6.0D, 3.0D, 10.0D, 2);

   public DoubleValue timerDelay = new DoubleValue(7, "Time", this, 4000.0D, 0.0D, 50000.0D, 0);

   public BooleanValue esp = new BooleanValue(2, "Esp", this, true);

   public BooleanValue onlyWhenNeed = new BooleanValue(19, "OnlyWhenNeed", this, true);

   public BooleanValue player = new BooleanValue(9, "Player", this, true);

   public BooleanValue mob = new BooleanValue(10, "Mob", this, true);

   public BooleanValue animal = new BooleanValue(11, "Animal", this, true);

   public BooleanValue villager = new BooleanValue(12, "Villager", this, true);

   public BooleanValue armorStand = new BooleanValue(13, "ArmorStand", this, true);

   public BooleansSetting targets = new BooleansSetting(31, "Targets", this, new Setting[]{this.player, this.mob, this.animal, this.villager, this.armorStand});

   public BooleanValue onlyKillAura = new BooleanValue(18, "OnlyKillAura", this, true);

   public DoubleValue range = new DoubleValue(5, "PreAimRange", this, 4.0D, 0.0D, 15.0D, 1);

   public BooleanValue packetVelocity = new BooleanValue(14, "Velocity", this, true);

   public BooleanValue packetVelocityExplosion = new BooleanValue(15, "ExplosionVelocity", this, true);

   public BooleanValue packetTimeUpdate = new BooleanValue(16, "TimeUpdate", this, true);

   public BooleanValue packetKeepAlive = new BooleanValue(17, "KeepAlive", this, true);

   public BooleansSetting packetsToDelay = new BooleansSetting(32, "PacketsToDelay", this, new Setting[]{this.packetVelocity, this.packetVelocityExplosion, this.packetTimeUpdate, this.packetKeepAlive});

   private EntityLivingBase entity = null;

   private boolean blockPackets;

   private WorldClient lastWorld;

   private INetHandler packetListener = null;
   public BooleanValue onWorld = new BooleanValue(123, "DisableOnWorld", this, false);
   @EventTarget
   public void onWorld(EventWorld eventWorld) {
      if(onWorld.getBoolean()) {
         setToggled(false);
      }
   }

   public BackTrack() {
      super("BackTrack", new Color(110, 186, 9), Categorys.COMBAT);
   }

   public void onEnable() {
      super.onEnable();
      this.blockPackets = false;
      this.b = true;
      if (mc.theWorld != null && mc.thePlayer != null) {
         for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
               EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
               entityLivingBase.realPosX = entityLivingBase.serverPosX;
               entityLivingBase.realPosZ = entityLivingBase.serverPosZ;
               entityLivingBase.realPosY = entityLivingBase.serverPosY;
            }
         }
      }

   }

   public void onDisable() {
      super.onDisable();
      if (this.packets.size() > 0 && this.packetListener != null) {
         this.resetPackets(this.packetListener);
      }

      this.packets.clear();
   }

   @EventTarget
   public void onEventEarlyTick(EventEarlyTick eventEarlyTick) {
      if (mm.killAura.isToggled()) {
         this.entity = KillAura.target;
      } else {
         Object[] listOfTargets = mc.theWorld.loadedEntityList.stream().filter(this::canAttacked).sorted(Comparator.comparingDouble((entityy) -> (double) mc.thePlayer.getDistanceToEntity(entityy))).toArray();
         if (listOfTargets.length > 0) {
            this.entity = (EntityLivingBase) listOfTargets[0];
         }

         if (this.onlyKillAura.getBoolean()) {
            this.entity = null;
         }
      }

      if (this.entity != null && mc.thePlayer != null && this.packetListener != null && mc.theWorld != null) {
         double d0 = (double) this.entity.realPosX / 32.0D;
         double d1 = (double) this.entity.realPosY / 32.0D;
         double d2 = (double) this.entity.realPosZ / 32.0D;
         double d3 = (double) this.entity.serverPosX / 32.0D;
         double d4 = (double) this.entity.serverPosY / 32.0D;
         double d5 = (double) this.entity.serverPosZ / 32.0D;
         float f = this.entity.width / 2.0F;
         AxisAlignedBB entityServerPos = new AxisAlignedBB(d3 - (double) f, d4, d5 - (double) f, d3 + (double) f, d4 + (double) this.entity.height, d5 + (double) f);
         Vec3 positionEyes = mc.thePlayer.getPositionEyes(mc.getTimer().renderPartialTicks);
         double currentX = MathHelper.clamp_double(positionEyes.xCoord, entityServerPos.minX, entityServerPos.maxX);
         double currentY = MathHelper.clamp_double(positionEyes.yCoord, entityServerPos.minY, entityServerPos.maxY);
         double currentZ = MathHelper.clamp_double(positionEyes.zCoord, entityServerPos.minZ, entityServerPos.maxZ);
         AxisAlignedBB entityPosMe = new AxisAlignedBB(d0 - (double) f, d1, d2 - (double) f, d0 + (double) f, d1 + (double) this.entity.height, d2 + (double) f);
         double realX = MathHelper.clamp_double(positionEyes.xCoord, entityPosMe.minX, entityPosMe.maxX);
         double realY = MathHelper.clamp_double(positionEyes.yCoord, entityPosMe.minY, entityPosMe.maxY);
         double realZ = MathHelper.clamp_double(positionEyes.zCoord, entityPosMe.minZ, entityPosMe.maxZ);
         double distance = this.hitRange.getValue();
         if (!mc.thePlayer.canEntityBeSeen(this.entity)) {
            distance = Math.min(distance, 3.0D);
         }

         double collision = this.entity.getCollisionBorderSize();
         double width = mc.thePlayer.width / 2.0F;
         double mePosXForPlayer = mc.thePlayer.getLastServerPosition().xCoord + (mc.thePlayer.getSeverPosition().xCoord - mc.thePlayer.getLastServerPosition().xCoord) / (double) MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
         double mePosYForPlayer = mc.thePlayer.getLastServerPosition().yCoord + (mc.thePlayer.getSeverPosition().yCoord - mc.thePlayer.getLastServerPosition().yCoord) / (double) MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
         double mePosZForPlayer = mc.thePlayer.getLastServerPosition().zCoord + (mc.thePlayer.getSeverPosition().zCoord - mc.thePlayer.getLastServerPosition().zCoord) / (double) MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
         AxisAlignedBB mePosForPlayerBox = new AxisAlignedBB(mePosXForPlayer - width, mePosYForPlayer, mePosZForPlayer - width, mePosXForPlayer + width, mePosYForPlayer + (double) mc.thePlayer.height, mePosZForPlayer + width);
         mePosForPlayerBox = mePosForPlayerBox.expand(collision, collision, collision);
         Vec3 entityPosEyes = new Vec3(d3, d4 + (double) this.entity.getEyeHeight(), d5);
         double bestX = MathHelper.clamp_double(entityPosEyes.xCoord, mePosForPlayerBox.minX, mePosForPlayerBox.maxX);
         double bestY = MathHelper.clamp_double(entityPosEyes.yCoord, mePosForPlayerBox.minY, mePosForPlayerBox.maxY);
         double bestZ = MathHelper.clamp_double(entityPosEyes.zCoord, mePosForPlayerBox.minZ, mePosForPlayerBox.maxZ);
         boolean b = entityPosEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 3.0D || mc.thePlayer.hurtTime < 8 && mc.thePlayer.hurtTime > 3;

         if (!this.onlyWhenNeed.getBoolean()) {
            b = true;
         }

         if (b && positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) && mc.thePlayer.getSeverPosition().distanceTo(new Vec3(d0, d1, d2)) < distance && !this.timeHelper.reached((long) this.timerDelay.getValue())) {
            this.blockPackets = true;
         } else {
            this.blockPackets = false;
            this.resetPackets(this.packetListener);
            this.timeHelper.reset();
         }
      }

   }

   @EventTarget
   public synchronized void onEventReadPacket(EventReadPacket eventReadPacket) {
      if (eventReadPacket.getNetHandler() != null) {
         this.packetListener = eventReadPacket.getNetHandler();
      }

      if (eventReadPacket.getDirection() == EnumPacketDirection.CLIENTBOUND) {
         Packet p = eventReadPacket.getPacket();
         if (p instanceof S08PacketPlayerPosLook) {
            this.resetPackets(eventReadPacket.getNetHandler());
         }

         Entity entity1;
         EntityLivingBase entityLivingBase;
         if (p instanceof S14PacketEntity) {
            S14PacketEntity packet = (S14PacketEntity) p;
            entity1 = mc.theWorld.getEntityByID(packet.getEntityId());
            if (entity1 instanceof EntityLivingBase) {
               entityLivingBase = (EntityLivingBase) entity1;
               entityLivingBase.realPosX += packet.func_149062_c();
               entityLivingBase.realPosY += packet.func_149061_d();
               entityLivingBase.realPosZ += packet.func_149064_e();
            }
         }

         if (p instanceof S18PacketEntityTeleport) {
            S18PacketEntityTeleport packet = (S18PacketEntityTeleport) p;
            entity1 = mc.theWorld.getEntityByID(packet.getEntityId());
            if (entity1 instanceof EntityLivingBase) {
               entityLivingBase = (EntityLivingBase) entity1;
               entityLivingBase.realPosX = packet.getX();
               entityLivingBase.realPosY = packet.getY();
               entityLivingBase.realPosZ = packet.getZ();
            }
         }

         if (this.entity == null) {
            this.resetPackets(eventReadPacket.getNetHandler());
         } else {
            if (mc.theWorld != null && mc.thePlayer != null) {
               if (this.lastWorld != mc.theWorld) {
                  this.resetPackets(eventReadPacket.getNetHandler());
                  this.lastWorld = mc.theWorld;
                  return;
               }

               this.addPackets(p, eventReadPacket);
            }

            this.lastWorld = mc.theWorld;
         }
      }
   }

   private boolean canAttacked(Entity entity) {
      if (entity instanceof EntityLivingBase) {
         if (entity.isInvisible()) {
            return false;
         }

         if (((EntityLivingBase) entity).deathTime > 1) {
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

         if (entity.ticksExisted < 50) {
            return false;
         }

         if (entity instanceof EntityPlayer && mm.teams.isToggled() && mm.teams.getTeammates().contains(entity)) {
            return false;
         }

         if (entity instanceof EntityPlayer && (entity.getName().equals("§aShop") || entity.getName().equals("SHOP") || entity.getName().equals("UPGRADES"))) {
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

      return entity instanceof EntityLivingBase && entity != mc.thePlayer && (double) mc.thePlayer.getDistanceToEntity(entity) < this.range.getValue();
   }

   @EventTarget
   public void onEventRender3D(EventRender3D eventRender3D) {
      if (this.esp.getBoolean()) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(2848);
         GL11.glDisable(2929);
         GL11.glDisable(3553);
         GlStateManager.disableCull();
         GL11.glDepthMask(false);
         if (this.entity != null && this.blockPackets) {
            this.render(this.entity);
         }

         GL11.glDepthMask(true);
         GlStateManager.enableCull();
         GL11.glEnable(3553);
         GL11.glEnable(2929);
         GL11.glDisable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(2848);
      }

   }

   private void render(EntityLivingBase entity) {
      float red = 0.0F;
      float green = 1.1333333F;
      float blue = 0.0F;
      float lineWidth = 3.0F;
      float alpha = 0.03137255F;
      if (mc.thePlayer.getDistanceToEntity(entity) > 1.0F) {
         double d0 = 1.0F - mc.thePlayer.getDistanceToEntity(entity) / 20.0F;
         if (d0 < 0.3D) {
            d0 = 0.3D;
         }

         lineWidth = (float) ((double) lineWidth * d0);
      }

      RenderUtil.drawEntityServerESP(entity, red, green, blue, alpha, 1.0F, lineWidth);
   }

   private void resetPackets(INetHandler netHandler) {
      if (this.packets.size() > 0) {
         for (; this.packets.size() != 0; this.packets.remove(this.packets.get(0))) {
            Packet packet = this.packets.get(0);

            try {
               if (packet != null) {
                  if (mm.velocity.isToggled() && (mm.velocity.mode.getSelected().equals("Spoof") || mm.velocity.mode.getSelected().equals("Basic") && mm.velocity.XZValue.getValue() == 0.0D && mm.velocity.YValue.getValue() == 0.0D)) {
                     if (!(packet instanceof S12PacketEntityVelocity) && (!(packet instanceof S27PacketExplosion) || !mm.velocity.ignoreExplosion.getBoolean() || !mm.velocity.mode.getSelected().equals("Basic"))) {
                        packet.processPacket(netHandler);
                     }
                  } else {
                     packet.processPacket(netHandler);
                  }
               }
            } catch (ThreadQuickExitException ignored) {}
         }
      }

   }

   private void addPackets(Packet packet, EventReadPacket eventReadPacket) {
      synchronized (this.packets) {
         if (this.delayPackets(packet)) {
            this.aBoolean = true;
            this.packets.add(packet);
            eventReadPacket.setCanceled(true);
         }

      }
   }

   private boolean delayPackets(Packet packet) {
      if (mc.currentScreen != null) {
         return false;
      } else if (packet instanceof S03PacketTimeUpdate) {
         return this.packetTimeUpdate.getBoolean();
      } else if (packet instanceof S00PacketKeepAlive) {
         return this.packetKeepAlive.getBoolean();
      } else if (packet instanceof S12PacketEntityVelocity) {
         return this.packetVelocity.getBoolean();
      } else if (packet instanceof S27PacketExplosion) {
         return this.packetVelocityExplosion.getBoolean();
      } else if (packet instanceof S19PacketEntityStatus) {
         S19PacketEntityStatus entityStatus = (S19PacketEntityStatus) packet;
         return entityStatus.getOpCode() != 2 || !(mc.theWorld.getEntityByID(entityStatus.getEntityId()) instanceof EntityLivingBase);
      } else {
         return !(packet instanceof S06PacketUpdateHealth) && !(packet instanceof S29PacketSoundEffect) && !(packet instanceof S3EPacketTeams) && !(packet instanceof S0CPacketSpawnPlayer);
      }
   }
}