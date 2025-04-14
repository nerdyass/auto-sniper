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

public class AutoRqCmd extends CommandBase {
    private final pC checker;

    final String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "N" + EnumChatFormatting.GRAY + "] ";

    public AutoRqCmd(pC checker) {
        this.checker = checker;
    }

    @Override
    public String getCommandName() {
        return "autorq";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/autorq - Toggle Auto-RQ on or off.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        checker.setAutoRqEnabled(!checker.isAutoRqEnabled());

        String status = checker.isAutoRqEnabled() ? EnumChatFormatting.AQUA + "enabled" : EnumChatFormatting.RED + "disabled";

        updateAutoRqConfig(checker.isAutoRqEnabled());

        sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.GREEN + "Auto-RQ is now " + status));
    }

    private void updateAutoRqConfig(boolean enabled) {
        File configFile = new File("config/autosniper.json");

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reader);
                JsonObject existingConfig = element.getAsJsonObject();

                existingConfig.addProperty("autoRqEnabled", enabled);

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
