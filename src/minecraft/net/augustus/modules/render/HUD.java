package net.augustus.modules.render;

import java.awt.Color;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.ColorSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;

public class HUD extends Module {
   public StringValue side = new StringValue(1, "Side", this, "Left", new String[]{"Left", "Right"});
   public StringValue mode = new StringValue(4, "Mode", this, "Basic", new String[]{"None", "Basic", "Other", "Ryu", "Old", "CSGO"});
   public DoubleValue size = new DoubleValue(8, "Size", this, 0.6, 0.01, 2.0, 2);
   public BooleanValue hotBar = new BooleanValue(5, "Hotbar", this, true);
   public BooleanValue armor = new BooleanValue(7, "Armor", this, true);
   public BooleanValue targethud = new BooleanValue(7, "TargetHud", this, true);
   public StringValue targetMode = new StringValue(69420, "TargetHud-Mode", this, "Basic", new String[]{"Basic", "Other"});
   public BooleanValue backGround = new BooleanValue(2, "BackGround", this, true);
   public ColorSetting backGroundColor = new ColorSetting(3, "BackGroundColor", this, new Color(0, 0, 0, 100));
   public ColorSetting color = new ColorSetting(6, "Color", this, Color.white);
   public DoubleValue targethud_x = new DoubleValue(12312124, "Targethud X", this, 0, 0, 2000, 0);
   public DoubleValue targethud_y = new DoubleValue(12312123, "Targethud Y", this, 0, 0, 2000, 0);

   public HUD() {
      super("HUD", new Color(75, 166, 91), Categorys.RENDER);
   }
}
