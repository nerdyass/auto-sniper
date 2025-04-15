package ass.nerdy.autosniper;

import net.minecraft.scoreboard.Team;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import static ass.nerdy.autosniper.AutoSniper.mc;

public class Util {
    public static int parseIntSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public static String getFormattedDisplayName(String playerName) {
        Team playerTeam = mc.world.getScoreboard().getScoreHolderTeam(playerName);
        if (playerTeam == null) {
            return "NONE";
        }
        int length = playerTeam.getColor().getName().length();
        if (length == 10) {
            return playerTeam.getPrefix() + playerName + playerTeam.getSuffix();
        }
        return "NONE";
    }

    public static void playSound(SoundEvent soundEvent, SoundCategory category, float volume, float pitch) {
        if (mc.player == null) return;
        mc.world.playSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), soundEvent, category, volume, pitch, false);
    }
}