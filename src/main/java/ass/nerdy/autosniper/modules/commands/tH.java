package ass.nerdy.autosniper.modules.commands;

import ass.nerdy.autosniper.modules.pC;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class tH extends CommandBase {
    final String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "N" + EnumChatFormatting.GRAY + "] ";

    private final pC checker;

    public tH(pC checker) {
        this.checker = checker;
    }

    @Override
    public String getCommandName() {
        return "hud";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hud - Toggle the visibility of the HUD.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        checker.tHudVisibility();

        boolean currentStatus = checker.isHudVisible();

        updateHudConfig(currentStatus);

        sender.addChatMessage(
                new ChatComponentText(
                        prefix + EnumChatFormatting.GREEN + "HUD is now " + (currentStatus
                                                                             ? EnumChatFormatting.AQUA + "visible"
                                                                             : EnumChatFormatting.RED + "hidden") + "."
                )
        );
    }

    private void updateHudConfig(boolean visible) {
        File configFile = new File("config/autosniper.json");

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reader);
                JsonObject existingConfig = element.getAsJsonObject();

                existingConfig.addProperty("hudVisible", visible);

                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(existingConfig.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
