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

        if (AutoSniper.config.setFKDRValue(args[0])) {
            AutoSniper.log(EnumChatFormatting.GREEN + "Minimum FKDR set to: " + EnumChatFormatting.AQUA + args[0]);
        } else {
            AutoSniper.log(EnumChatFormatting.RED + "Invalid number");
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
