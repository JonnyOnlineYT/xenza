package net.augustus;

import lombok.Getter;
import lombok.Setter;
import me.jDev.xenza.files.Converter;
import net.augustus.cleanGui.CleanClickGui;
import net.augustus.clickgui.ClickGui;
import net.augustus.clickgui.SettingSorter;
import net.augustus.commands.CommandManager;
import net.augustus.font.testfontbase.FontUtil;
import net.augustus.modules.Manager;
import net.augustus.modules.ModuleManager;
import net.augustus.notify.rise5.NotificationManager;
import net.augustus.settings.SettingsManager;
import net.augustus.ui.augustusmanager.AugustusSounds;
import net.augustus.utils.BlockUtil;
import net.augustus.utils.ColorUtil;
import net.augustus.utils.YawPitchHelper;
import net.augustus.utils.shader.BackgroundShaderUtil;
import net.augustus.utils.skid.lorious.font.Fonts;
import net.lenni0451.eventapi.manager.EventManager;
import viamcp.ViaMCP;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Augustus {
   private static final Augustus instance = new Augustus();
   private String name = null;
   private String version = "reborn b2";
   private String dev = null;
   private final Color clientColor = new Color(41, 146, 222);
   private List<String> lastAlts = new ArrayList<>();
   private final Manager manager = new Manager();
   private ModuleManager moduleManager;
   private SettingsManager settingsManager;
   private CommandManager commandManager;
   private CleanClickGui cleanClickGui;
   private ClickGui clickGui;
   private Converter converter;
   private BackgroundShaderUtil backgroundShaderUtil;
   private float shaderSpeed = 1800.0F;
   private SettingSorter settingSorter;
   private YawPitchHelper yawPitchHelper;
   public String uid = "";
   private BlockUtil blockUtil;
   private Fonts loriousFontService;
   private Proxy proxy;
   private NotificationManager rise5notifyManager;
   private ColorUtil colorUtil;

   public static Augustus getInstance() {
      return instance;
   }

   public void preStart() {
      Path dir = Paths.get("xenzarecode/configs");
      if (!Files.exists(dir)) {
         try {
            Files.createDirectories(dir);
         } catch (IOException var2) {
            var2.printStackTrace();
         }
      }
   }

   public void start() {
      name = "xenza";
      dev = "jDev + Cookie + SaFeBaum";
      System.out.println("Starting Client...");
      FontUtil.bootstrap();
      this.loriousFontService = new Fonts();
      this.loriousFontService.bootstrap();
      this.colorUtil = new ColorUtil();
      this.yawPitchHelper = new YawPitchHelper();
      this.settingsManager = new SettingsManager();
      this.moduleManager = new ModuleManager();
      this.commandManager = new CommandManager();
      this.rise5notifyManager = new NotificationManager();
      this.clickGui = new ClickGui("ClickGui");
      this.converter = new Converter();
      this.converter.settingReader(this.settingsManager.getStgs());
      this.converter.settingSaver(this.settingsManager.getStgs());
      this.converter.moduleReader(this.moduleManager.getModules());
      this.converter.readLastAlts();
      this.converter.clickGuiLoader(this.clickGui.getCategoryButtons());
      this.backgroundShaderUtil = new BackgroundShaderUtil();
      this.settingSorter = new SettingSorter();
      this.cleanClickGui = new CleanClickGui();
      AugustusSounds.currentSound = this.converter.readSound();
      this.blockUtil = new BlockUtil();
      EventManager.register(this.settingSorter);
      EventManager.register(this);
      try {
         ViaMCP.getInstance().start();
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }
}
