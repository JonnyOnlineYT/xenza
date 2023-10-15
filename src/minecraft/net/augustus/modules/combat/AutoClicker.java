package net.augustus.modules.combat;

import java.awt.Color;
import net.augustus.events.EventClick;
import net.augustus.events.EventTick;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.RandomUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public class AutoClicker extends Module {
   public final DoubleValue minDelay = new DoubleValue(1, "MinDelay", this, 50.0, 0.0, 400.0, 0);
   public final DoubleValue maxDelay = new DoubleValue(5, "MaxDelay", this, 110.0, 0.0, 400.0, 0);
   public final BooleanValue smart = new BooleanValue(20, "Smart", this, false);
   public final BooleanValue onlyOnTarget = new BooleanValue(2, "OnlyOnTarget", this, false);
   private final TimeHelper timeHelper = new TimeHelper();
   private long randomDelay = 100L;

   public AutoClicker() {
      super("AutoClicker", new Color(82, 186, 74), Categorys.COMBAT);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.randomDelay = 0L;
   }

   @EventTarget
   public void onEventTick(EventTick eventTick) {
   }

   @EventTarget
   public void onEventClick(EventClick eventClick) {
      if (Mouse.isButtonDown(0) && mc.currentScreen == null) {
         if (!mc.thePlayer.isUsingItem()) {
            if (this.attack()) {
               if (this.onlyOnTarget.getBoolean()) {
                  if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                     mc.clickMouse();
                     this.timeHelper.reset();
                     this.setRandomDelay();
                  }
               } else {
                  mc.clickMouse();
                  this.timeHelper.reset();
                  this.setRandomDelay();
               }
            }

            while(mc.gameSettings.keyBindUseItem.isPressed()) {
               mc.rightClickMouse();
            }
         } else {
            if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
               mc.playerController.onStoppedUsingItem(mc.thePlayer);
            }

            while(mc.gameSettings.keyBindAttack.isPressed()) {
            }

            while(mc.gameSettings.keyBindUseItem.isPressed()) {
            }

            while(mc.gameSettings.keyBindPickBlock.isPressed()) {
            }
         }

         if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.getRightClickDelayTimer() == 0 && !mc.thePlayer.isUsingItem()) {
            mc.rightClickMouse();
         }

         if (mc.currentScreen == null) {
         }

         mc.sendClickBlockToController(false);
         eventClick.setCanceled(true);
      }
   }

   private boolean attack() {
      if (this.smart.getBoolean()
         && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
         && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)mc.objectMouseOver.entityHit;
         if (entity.hurtTime == 0 || entity.hurtTime == 1) {
            return true;
         }
      }

      return this.timeHelper.reached(this.randomDelay);
   }

   private void setRandomDelay() {
      this.randomDelay = (long)RandomUtil.nextSecureInt((int)this.minDelay.getValue(), (int)this.maxDelay.getValue());
   }
}
