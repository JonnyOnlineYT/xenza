package net.augustus.modules.misc;

import net.augustus.Augustus;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.PlayerUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.*;
import java.util.HashMap;

public class Spammer extends Module {
    public Spammer() {
        super("Spammer", new Color(73, 26, 245), Categorys.MISC);
    }

    private final char[] chars = new char[]{'⛟', '⛠', '⛡', '⛢', '⛣', '⛤', '❓', '⸻'};

    private static final HashMap<Character, Character> lookalikes = new HashMap<Character, Character>() {{
        put('a', 'а'); // Alpha
        put('b', 'Ƅ'); // Beta
        put('c', 'с'); // Lunate sigma
        put('d', 'ԁ'); // Partial derivative
        put('e', 'е'); // Epsilon
        put('f', 'ẝ'); // Phi
        put('g', 'ɡ'); // Gamma
        put('h', 'һ'); // Lambda
        put('i', 'і'); // Iota
        put('j', 'ϳ'); // Cyrillic je
        put('k', 'κ'); // Kappa
        put('l', 'ӏ'); // Lambda
        put('m', 'ⅿ'); // Mu
        put('n', 'ո'); // Nu
        put('o', 'о'); // Omicron
        put('p', 'р'); // Pi
        put('q', 'զ'); // Cyrillic ka with hook
        put('r', 'г'); // Rho
        put('s', 'ꜱ'); // Sigma
        put('t', 't'); // Tau
        put('u', 'υ'); // Upsilon
        put('v', 'ν'); // Nu
        put('w', 'ԝ'); // Omega
        put('x', 'х'); // Chi
        put('y', 'у'); // Upsilon with hook
        put('z', 'ʐ'); // Zeta
        /*
        put('A', '\u0391'); // Alpha capital
        put('B', '\u0392'); // Beta capital
        put('C', '\u03A7'); // Chi capital
        put('D', '\u2206'); // Delta capital
        put('E', '\u0395'); // Epsilon capital
        put('F', '\u03A6'); // Phi capital
        put('G', '\u0393'); // Gamma capital
        put('H', '\u0397'); // Eta capital
        put('I', '\u0399'); // Iota capital
        put('J', '\u0408'); // Cyrillic je capital
        put('K', '\u039A'); // Kappa capital
        put('L', '\u039B'); // Lambda capital
        put('M', '\u039C'); // Mu capital
        put('N', '\u039D'); // Nu capital
        put('O', '\u039F'); // Omicron capital
        put('P', '\u03A0'); // Pi capital
        put('Q', '\u051A'); // Cyrillic ka with hook and tail capital
        put('R', '\u03A1'); // Rho capital
        put('S', '\u03A3'); // Sigma capital
        put('T', '\u03A4'); // Tau capital
        put('U', '\u03A5'); // Upsilon capital
        put('V', '\u039E'); // Xi capital
        put('W', '\u03A9'); // Omega capital
        put('X', '\u03A7'); // Chi capital
        put('Y', '\u03A5'); // Upsilon capital
        put('Z', '\u0396'); // Zeta capital
         */
    }};
    public static String[] never_gonna = {
            "We're no strangers to love",
            "You know the rules and so do I (do I)",
            "A full commitment's what I'm thinking of",
            "You wouldn't get this from any other guy",
            "I just wanna tell you how I'm feeling",
            "Gotta make you understand",
            "Never gonna give you up",
            "Never gonna let you down",
            "Never gonna run around and desert you",
            "Never gonna make you cry",
            "Never gonna say goodbye",
            "Never gonna tell a lie and hurt you",
            "We've known each other for so long",
            "Your heart's been aching, but you're too shy to say it (say it)",
            "Inside, we both know what's been going on (going on)",
            "We know the game and we're gonna play it",
            "And if you ask me how I'm feeling",
            "Don't tell me you're too blind to see",
            "Never gonna give you up",
            "Never gonna let you down",
            "Never gonna run around and desert you",
            "Never gonna make you cry",
            "Never gonna say goodbye",
            "Never gonna tell a lie and hurt you",
            "Never gonna give you up",
            "Never gonna let you down",
            "Never gonna run around and desert you",
            "Never gonna make you cry",
            "Never gonna say goodbye",
            "Never gonna tell a lie and hurt you",
            "We've known each other for so long",
            "Your heart's been aching, but you're too shy to say it (to say it)",
            "Inside, we both know what's been going on (going on)",
            "We know the game and we're gonna play it",
            "I just wanna tell you how I'm feeling",
            "Gotta make you understand",
            "Never gonna give you up",
            "Never gonna let you down",
            "Never gonna run around and desert you",
            "Never gonna make you cry",
            "Never gonna say goodbye",
            "Never gonna tell a lie and hurt you",
            "Never gonna give you up",
            "Never gonna let you down",
            "Never gonna run around and desert you",
            "Never gonna make you cry",
            "Never gonna say goodbye",
            "Never gonna tell a lie and hurt you",
            "Never gonna give you up",
            "Never gonna let you down",
            "Never gonna run around and desert you",
            "Never gonna make you cry",
            "Never gonna say goodbye",
            "Never gonna tell a lie and hurt you"
    };

    public static String replaceWithUnicodeLookalike(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            char lookalike = lookalikes.getOrDefault(ch, ch);
            result.append(lookalike);
        }
        return result.toString();
    }

    private int index;
    //private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, Spammer.Mode.values());
    //private final NumberValue delay = new NumberValue("Delay", this, 3, 1, 10, true);
    private final StringValue enumValue = new StringValue(1837699, "EnumValue", this, "RickRoll", new String[]{"Custom", "RickRoll", "Sus", "Sponsor"});
    private final DoubleValue delay = new DoubleValue(298637, "Delay", this, 3, 0, 10, 0);
    private final BooleanValue bipas = new BooleanValue(0x24A6, "Bipas", this, false);
    public BooleanValue onWorld = new BooleanValue(123, "DisableOnWorld", this, false);
    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        if(onWorld.getBoolean()) {
            setToggled(false);
        }
    }
    private final TimeHelper stopwatch = new TimeHelper();
    private final String[] cum = {
            "*intensive moaning*",
            "im gonna cum :D",
            "get xenza for insane waifus",
            "i love to cum uwu",
            "femboys are veri cute",
            "get xenza with minecraft sex mod support",
            "get an big orgasm now with xenza",
            "imma just get your moms pregnant using xenza",
            "B==)~ {()}",
            "uwu",
            "get xenza or no sex",
            "sex = gud",
            "my dick is so hard because of xenza's waifu support",
            "pornhub.com = free robux :pray:",
            "want to get infinite porn hub premium? buy xenza at jDev#7905",
            "want to suck of your homies dick? get xenza at jDev#7905",
            "want to get hard immediately? get xenza by jDev#7905",
            "femboys are on your way with xenza by jDev#7905",
            "wannt hallaal HINDI 2024 minecraft hackar clientert gut xenza at @ jDev#7905",
            "use spanky antihake please (jDev#7905)"
    };
    /*
    private final String[] jakePaul = {
            "Y'all can't handle this\n",
            "Y'all don't know what's about to happen baby\n",
            "Team 10\n",
            "Los Angeles, Cali boy\n",
            "But I'm from Ohio though, white boy\n",
            "It's everyday bro, with the Disney Channel flow\n",
            "5 mill on YouTube in 6 months, never done before\n",
            "Passed all the competition man, PewDiePie is next\n",
            "Man I'm poppin' all these checks, got a brand new Rolex\n",
            "And I met a Lambo too and I'm coming with the crew\n",
            "This is Team 10, bitch, who the hell are flippin' you?\n",
            "And you know I kick them out if they ain't with the crew\n",
            "Yeah, I'm talking about you, you beggin' for attention\n",
            "Talking shit on Twitter too but you still hit my phone last night\n",
            "It was 4:52 and I got the text to prove\n",
            "And all the recordings too, don't make me tell them the truth\n",
            "And I just dropped some new merch and it's selling like a god, church\n",
            "Ohio's where I'm from, we chew 'em like it's gum\n",
            "We shooting with a gun, the tattoo just for fun\n",
            "I Usain Bolt and run, catch me at game one\n",
            "I cannot be outdone, Jake Paul is number one\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "I said it is everyday bro!\n",
            "You know it's Nick Crompton and my collar stay poppin'\n",
            "Yes, I can rap and no, I am not from Compton\n",
            "England is my city\n",
            "And if it weren't for Team 10, then the US would be shitty\n",
            "I'll pass it to Chance 'cause you know he stay litty\n",
            "Two months ago you didn't know my name\n",
            "And now you want my fame? Bitch I'm blowin' up\n",
            "I'm only going up, now I'm going off, I'm never fallin' off\n",
            "Like Mag, who? Digi who? Who are you?\n",
            "All these beefs I just ran through, hit a milli in a month\n",
            "Where were you? Hatin' on me back in West Fake\n",
            "You need to get your shit straight\n",
            "Jakey brought me to the top, now we're really poppin' off\n",
            "Number one and number four, that's why these fans all at our door\n",
            "It's lonely at the top so we all going\n",
            "We left Ohio, now the trio is all rollin'\n",
            "It's Team 10, bitch\n",
            "We back again, always first, never last\n",
            "We the future, we'll see you in the past\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "I said it is everyday bro!\n",
            "Hold on, hold on, hold on, hold on (espera)\n",
            "Can we switch the language? (Ha, ya tÃº sabes)\n",
            "We 'bout to hit it (dale)\n",
            "SÃ­, lo unico que quiero es dinero\n",
            "Trabajando en YouTube todo el dÃ­a entero\n",
            "Viviendo en U.S.A, el sueÃ±o de cualquiera\n",
            "Enviando dÃ³lares a mi familia entera\n",
            "Tenemos una persona por encima\n",
            "Se llama Donald Trump y estÃ¡ en la cima\n",
            "Desde aquÃ­ te cantamos can I get my VISA?\n",
            "Martinez Twins, representando EspaÃ±a\n",
            "Desde la pobreza a la fama\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "I said it is everyday bro!\n",
            "Yo, it's Tessa Brooks\n",
            "The competition shook\n",
            "These guys up on me\n",
            "I got 'em with the hook\n",
            "Lemme educate ya'\n",
            "And I ain't talking book\n",
            "Panera is your home?\n",
            "So, stop calling my phone\n",
            "I'm flyin' like a drone\n",
            "They buying like a loan\n",
            "Yeah, I smell good\n",
            "Is that your boy's cologne?\n",
            "Is that your boy's cologne?\n",
            "Started balling', quicken Loans\n",
            "Now I'm in my flippin' zone\n",
            "Yes, they all copy me\n",
            "But, that's some shitty clones\n",
            "Stay in all designer clothes\n",
            "And they ask me what I make\n",
            "I said is 10 with six zeros\n",
            "Always plug, merch link in bio\n",
            "And I will see you tomorrow 'cause it's everyday bro\n",
            "Peace\n",
            "Ya tu sabes baby, Jake Paul"
    };
     */

    public static String spamString = Augustus.getInstance().getName() + " " + Augustus.getInstance().getVersion() + " by thebois industries";

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (stopwatch.reached((long) (delay.getValue() * 1000L))) {
            switch (enumValue.getSelected()) {
                case "Sponsor": {
                    if (RANDOM.nextBoolean()) {
                        mc.thePlayer.sendChatMessage("StepWise on top <3 discord.gg/muvpzCj8MA");
                    } else {
                        mc.thePlayer.sendChatMessage("cheap minecraft server hosting by StepWise - discord.gg/muvpzCj8MA");
                    }
                    stopwatch.reset();
                    break;
                }
                case "Sus": {
                    try {
                        mc.thePlayer.sendChatMessage(cum[RANDOM.nextInt(cum.length - 1)]);
                    } catch (
                            ArrayIndexOutOfBoundsException exc) {
                        System.out.println("HARAM!!!!!!!!!!111");
                    }
                    stopwatch.reset();
                    break;
                }
                case "RickRoll": {
                    try {
                        mc.thePlayer.sendChatMessage(never_gonna[index++]);
                    } catch (
                            ArrayIndexOutOfBoundsException exc) {
                        index = 0;
                    }
                    stopwatch.reset();
                    break;
                }
                case "Custom":
                    if(bipas.getBoolean()) {
                        mc.thePlayer.sendChatMessage(spamString + " - " + RandomStringUtils.randomAlphabetic(32));
                    } else {
                        mc.thePlayer.sendChatMessage(spamString);
                    }
                    stopwatch.reset();
                    break;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        stopwatch.reset();
        index = 0;
        PlayerUtil.sendChat("Use .spammer [message] to edit the spam message.");
    }
}
