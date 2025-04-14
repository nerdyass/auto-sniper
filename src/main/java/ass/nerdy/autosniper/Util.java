package ass.nerdy.autosniper;

import net.minecraft.scoreboard.ScorePlayerTeam;

import static ass.nerdy.autosniper.AutoSniper.mc;

// ik bad practise to put all utils in a Util file but whatever
public class Util {
    public static int parseIntSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public static String getFormattedDisplayName(String playerName) {
        ScorePlayerTeam playerTeam = mc.theWorld.getScoreboard().getPlayersTeam(playerName);
        if (playerTeam == null) {
            return "NONE";
        }
        int length = playerTeam.getColorPrefix().length();
        if (length == 10) {
            return playerTeam.getColorPrefix() + playerName + playerTeam.getColorSuffix();
        }
        return "NONE";
    }

    public static void playSound(String soundName, float volume, float pitch) {
        if (mc.thePlayer == null) return;
        mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, soundName, volume, pitch, false);
    }
}
