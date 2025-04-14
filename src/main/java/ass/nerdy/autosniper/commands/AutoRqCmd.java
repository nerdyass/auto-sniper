package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class AutoRqCmd extends CommandBase {
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
        AutoSniper.config.autoRqEnabled = !AutoSniper.config.autoRqEnabled;
        AutoSniper.config.save();

        AutoSniper.log(EnumChatFormatting.GREEN + "Auto-RQ is now " + (
                AutoSniper.config.autoRqEnabled
                ? EnumChatFormatting.AQUA + "enabled"
                : EnumChatFormatting.RED + "disabled"
        ));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
