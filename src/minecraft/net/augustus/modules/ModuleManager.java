package net.augustus.modules;

import java.util.ArrayList;
import java.util.List;

import net.augustus.modules.combat.*;
import net.augustus.modules.misc.*;
import net.augustus.modules.movement.Blink;
import net.augustus.modules.movement.BugUp;
import net.augustus.modules.movement.FastLadder;
import net.augustus.modules.movement.Fly;
import net.augustus.modules.movement.Jesus;
import net.augustus.modules.movement.LongJump;
import net.augustus.modules.movement.NoSlow;
import net.augustus.modules.movement.NoWeb;
import net.augustus.modules.movement.SafeWalk;
import net.augustus.modules.movement.Speed;
import net.augustus.modules.movement.Spider;
import net.augustus.modules.movement.Sprint;
import net.augustus.modules.movement.Step;
import net.augustus.modules.movement.Strafe;
import net.augustus.modules.movement.TargetStrafe;
import net.augustus.modules.movement.Timer;
import net.augustus.modules.movement.VClip;
import net.augustus.modules.player.AutoArmor;
import net.augustus.modules.player.AutoTool;
import net.augustus.modules.player.ChestStealer;
import net.augustus.modules.player.FakeLag;
import net.augustus.modules.player.Inventory;
import net.augustus.modules.player.InventoryCleaner;
import net.augustus.modules.player.NoFall;
import net.augustus.modules.player.Phase;
import net.augustus.modules.player.Regen;
import net.augustus.modules.player.Teleport;
import net.augustus.modules.render.*;
import net.augustus.modules.world.BlockFly;
import net.augustus.modules.world.FastBreak;
import net.augustus.modules.world.FastPlace;
import net.augustus.modules.world.Fucker;
import net.augustus.modules.world.NewScaffold;
import net.augustus.modules.world.ScaffoldWalk;
import net.augustus.utils.interfaces.MA;

import javax.swing.*;

public class ModuleManager implements MA {
   public final Velocity velocity = new Velocity();
   public final KillAura killAura = new KillAura();
   public final AntiBot antiBot = new AntiBot();
   public final Teams teams = new Teams();
   public final BackTrack backTrack = new BackTrack();
   public final AutoSoup autoSoup = new AutoSoup();
   public final AutoClicker autoClicker = new AutoClicker();
   public final AntiFireBall antiFireBall = new AntiFireBall();
   public final TimerRange timerRange = new TimerRange();
   public final Criticals criticals = new Criticals();
   public final MotionGraph motionGraph = new MotionGraph();
   public final MoreKB moreKB = new MoreKB();
   public final MurderThingyIdk murderThingyIdk = new MurderThingyIdk();
   public final net.augustus.modules.render.ArrayList arrayList = new net.augustus.modules.render.ArrayList();
   public final ClickGUI clickGUI = new ClickGUI();
   public final FastBow fastBow = new FastBow();
   public final AttackEffects attackEffects = new AttackEffects();
   public final ESP esp = new ESP();
   public final StorageESP storageESP = new StorageESP();
   public final BlockAnimation blockAnimation = new BlockAnimation();
   public final FullBright fullBright = new FullBright();
   public final Protector protector = new Protector();
   public final ChinaHat chinaHat = new ChinaHat();
   public final NameTags nameTags = new NameTags();
   public final HUD hud = new HUD();
   public final Tracers tracers = new Tracers();
   public final ItemEsp itemEsp = new ItemEsp();
   public final Scoreboard scoreboard = new Scoreboard();
   public final BlockESP blockESP = new BlockESP();
   public final Barriers barriers = new Barriers();
   public final Line line = new Line();
   public final Ambiance ambiance = new Ambiance();
   public final CrossHair crossHair = new CrossHair();
   public final Trajectories trajectories = new Trajectories();
   public final Projectiles projectiles = new Projectiles();
   public final CustomGlint customGlint = new CustomGlint();
   public final CustomItemPos customItemPos = new CustomItemPos();
   public final Sprint sprint = new Sprint();
   public final Jesus jesus = new Jesus();
   public final NoSlow noSlow = new NoSlow();
   public final Speed speed = new Speed();
   public final Timer timer = new Timer();
   public final Strafe strafe = new Strafe();
   public final Blink blink = new Blink();
   public final TestModule testModule = new TestModule();
   public final Fly fly = new Fly();
   public final BugUp bugUp = new BugUp();
   public final TargetStrafe targetStrafe = new TargetStrafe();
   public final FastLadder fastLadder = new FastLadder();
   public final Spider spider = new Spider();
   public final NoWeb noWeb = new NoWeb();
   public final Step step = new Step();
   public final LongJump longJump = new LongJump();
   public final VClip vclip = new VClip();
   public final SafeWalk safeWalk = new SafeWalk();
   public final NoFall noFall = new NoFall();
   public final AutoArmor autoArmor = new AutoArmor();
   public final InventoryCleaner inventoryCleaner = new InventoryCleaner();
   public final ChestStealer chestStealer = new ChestStealer();
   public static boolean cracked = true;
   public final Inventory inventory = new Inventory();
   public final Notifications notifications = new Notifications();

   public final AutoTool autoTool = new AutoTool();
   public final FakeLag fakeLag = new FakeLag();
   public final Teleport teleport = new Teleport();
   public final Phase phase = new Phase();
   public final Regen regen = new Regen();
   //public final ScaffoldWalk scaffoldWalk = new ScaffoldWalk();
   //public final NewScaffold newScaffold = new NewScaffold();
   public final FastBreak fastBreak = new FastBreak();
   public final Fucker fucker = new Fucker();
   public final FastPlace fastPlace = new FastPlace();
   public final BlockFly blockFly = new BlockFly();
   public final Spammer spammer = new Spammer();
   public final MidClick midClick = new MidClick();
   public final Disabler disabler = new Disabler();
   public final AutoPlay autoPlay = new AutoPlay();
   public final AutoRegister autoRegister = new AutoRegister();
   public final SpinBot spinBot = new SpinBot();
   public final Fixes fixes = new Fixes();
   public final AutoWalk autoWalk = new AutoWalk();
   public final StaffDetector staffDetector = new StaffDetector();
   public final Shaders shaders = new Shaders();

    public List<Module> getModules() {
      return ma.getModules();
   }

   public List<Module> getModules(Categorys cat) {
      List<Module> modules = new ArrayList<>();
      for(Module mod : getModules()) {
         if(mod.getCategory().equals(cat)) {
            modules.add(mod);
         }
      }
      return modules;
   }


   public void setModules(List<Module> modules) {
      ma.setModules(modules);
   }

   public ArrayList<Module> getActiveModules() {
      return ma.getActiveModules();
   }

   public void setActiveModules(ArrayList<Module> activeModules) {
      ma.setActiveModules(activeModules);
   }
}
