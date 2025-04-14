package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class KeyCmd extends CommandBase {
    @Override
    public String getCommandName() {
        return "key";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/key <apikey>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            AutoSniper.log(EnumChatFormatting.RED + "Usage: /key <apikey>");
            return;
        }

        String apiKey = args[0];

        String apiKeyName = "Hypixel API key";

        AutoSniper.config.apiKey = apiKey;
        if (AutoSniper.config.save()) {
            AutoSniper.log(EnumChatFormatting.LIGHT_PURPLE + apiKeyName + EnumChatFormatting.GREEN + " saved successfully!");
        } else {
            AutoSniper.log(EnumChatFormatting.RED + "Failed to save " + apiKeyName);
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
