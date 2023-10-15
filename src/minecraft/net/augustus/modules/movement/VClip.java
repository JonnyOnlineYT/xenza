package net.augustus.modules.movement;

import java.awt.Color;
import net.augustus.events.EventTick;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.lenni0451.eventapi.reflection.EventTarget;

public class VClip extends Module {
   public VClip() {
      super("VClip", new Color(63, 255, 0, 255), Categorys.MOVEMENT);
   }

   @EventTarget
   public void onEventTick(EventTick eventTick) {
      if (mc.gameSettings.keyBindSneak.isPressed() && !mm.fly.isToggled()) {
         mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3.0, mc.thePlayer.posZ);
         if ((mc.theWorld == null || mc.thePlayer == null) && mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater()) {
            mc.thePlayer.motionY -= 6.0;
         }
      }
   }
}
