package net.augustus.commands.commands;

import net.augustus.Augustus;
import net.augustus.commands.Command;
import net.augustus.modules.Module;

public class CommandToggle extends Command {
   public CommandToggle() {
      super(".t");
   }

   @Override
   public void commandAction(String[] message) {
      super.commandAction(message);
      if (message.length > 1) {
         for(Module module : Augustus.getInstance().getModuleManager().getModules()) {
            if (message[1].equalsIgnoreCase(module.getName())) {
               module.toggle();
               this.sendChat("§c" + module.getName() + " §7toggled");
               return;
            }
         }
      } else {
         this.sendChat("§7.t [§cModule§7]");
      }
   }

   @Override
   public void helpMessage() {
      this.sendChat("§c.t§7 (Toggles a module)");
   }
}
