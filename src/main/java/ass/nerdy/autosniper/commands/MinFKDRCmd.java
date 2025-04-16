package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class MinFKDRCmd extends CommandBase {
    @Override
    public String getCommandName() {
        return "minfkdr";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/minfkdr [fkdr value]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            AutoSniper.log(EnumChatFormatting.RED + "You must provide an FKDR value to set!");
            return;
        }

        String input = args[0];

        try {
            double fkdr = Float.parseFloat(input);
            if (fkdr < 0) {
                AutoSniper.log(EnumChatFormatting.RED + "FKDR cannot be negative.");
                return;
            }

            if (AutoSniper.config.setFKDRValue(input)) {
                AutoSniper.log(EnumChatFormatting.GREEN + "Minimum FKDR set to: " + EnumChatFormatting.AQUA + input);
            } else {
                AutoSniper.log(EnumChatFormatting.RED + "Failed to set FKDR.");
            }

        } catch (NumberFormatException e) {
            AutoSniper.log(EnumChatFormatting.RED + "Invalid number: FKDR must be a valid number (e.g., 1.5)");
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
