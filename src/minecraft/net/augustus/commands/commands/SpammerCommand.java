package net.augustus.commands.commands;

import net.augustus.Augustus;
import net.augustus.commands.Command;
import net.augustus.modules.misc.Spammer;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.Sys;

public class SpammerCommand extends Command {
    public SpammerCommand() {
        super(".spammer");
    }

    @Override
    public void commandAction(String[] message) {
        super.commandAction(message);
        try {
            Spammer.spamString = "";
            /*
            for (String str : message) {
                Spammer.spamString += str;
            }
            */
            for(int i = 1; i < message.length; i++) {
                Spammer.spamString += message[i] + " ";
            }
            this.sendChat("Successfully set the spammer message to " + message[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            this.sendChat("Do \".spammer *message*\"!");
        }
    }
}
