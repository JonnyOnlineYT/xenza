package net.minecraft.client.gui;

import net.augustus.Augustus;
import net.augustus.changeLog.ChangeLogType;
import net.augustus.utils.skid.lorious.ColorUtils;
import net.augustus.utils.skid.lorious.LoriousButton;
import net.augustus.utils.skid.lorious.Timer;
import net.augustus.utils.skid.lorious.anims.Animation;
import net.augustus.utils.skid.lorious.anims.Easings;
import net.augustus.utils.skid.ohare.font.Fonts;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiMainMenu
        extends GuiScreen {
   public Animation xPos = new Animation();
   public Animation yPos = new Animation();
   public Timer timer = new Timer();
   public boolean initialized;
   public List<LoriousButton> loriousButtons = new ArrayList();

   public void initGui() {
      ScaledResolution sr = new ScaledResolution(this.mc);
      this.xPos.animate(sr.getScaledWidth(), 1000.0, Easings.QUINT_OUT);
      this.yPos.animate(sr.getScaledHeight(), 1000.0, Easings.QUINT_OUT);
      if (!this.initialized) {
         this.timer.startTimer();
         this.initialized = true;
      }
      this.loriousButtons.add(new LoriousButton(0, 0, 25, "Single Player"));
      this.loriousButtons.add(new LoriousButton(1, 0, 47, "Multi Player"));
      this.loriousButtons.add(new LoriousButton(2, 0, 69, "Options"));
      this.loriousButtons.add(new LoriousButton(666, 0, 91, "Client Manager"));
      this.loriousButtons.add(new LoriousButton(420, 0, 113, "Alt Manager"));
      this.loriousButtons.add(new LoriousButton(3, 0, 135, "Exit"));
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution sr = new ScaledResolution(this.mc);
      final HashMap<String, ChangeLogType> changes = new HashMap();
      Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(25, 25, 25).getRGB());
      //changes.put("+ Motiongraph", ChangeLogType.ADD);

      if (sr.getScaledWidth() > 600 && sr.getScaledHeight() > 300) {
         Color color = new Color(200, 200, 200, 150);
         Fonts.hudfont.drawString("Changelog:", 5, 5, color.hashCode());

         int i = 0;
         for (Map.Entry<String, ChangeLogType> str : changes.entrySet()) {
            Fonts.hudfont.drawString(str.getKey(), 5, 16 + i * 12, str.getValue().getColor().getRGB() );
            i++;
         }
      }
      Augustus.getInstance().getLoriousFontService().getComfortaa38().drawCenteredString("xenza", (float)(this.xPos.getValue() / 2.0), (float)(this.yPos.getValue() / 2.0 - 20.0 - (sr.getScaledHeight() / 8)), -1, true, ColorUtils.getRainbow(4.0f, 0.5f, 1.0f), new Color(255, 255, 255));
      for (LoriousButton loriousButton : this.loriousButtons) {
         loriousButton.draw(mouseX, mouseY, (int)(this.xPos.getValue() / 2.0 - 100.0), (int)(this.yPos.getValue() / 2.0 - (sr.getScaledHeight() / 8)));
      }
      if (this.xPos.getTarget() != (double)sr.getScaledWidth()) {
         this.xPos.animate(sr.getScaledWidth(), 500.0, Easings.QUINT_OUT);
      }
      if (this.yPos.getTarget() != (double)sr.getScaledHeight()) {
         this.yPos.animate(sr.getScaledHeight(), 500.0, Easings.QUINT_OUT);
      }
      this.xPos.updateAnimation();
      this.yPos.updateAnimation();
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      for (LoriousButton loriousButton : this.loriousButtons) {
         loriousButton.onClick(mouseX, mouseY, mouseButton);
      }
   }
}
