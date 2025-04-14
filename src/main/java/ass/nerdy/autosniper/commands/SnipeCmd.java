package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import ass.nerdy.autosniper.Checker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class SnipeCmd extends CommandBase {
    private final Checker checker;

    public SnipeCmd(Checker checker) {
        this.checker = checker;
    }

    @Override
    public String getCommandName() {
        return "snipe";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/snipe - Toggle player checking on or off.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        checker.togglePlayerCheckEnabled();
        AutoSniper.config.save();
        AutoSniper.log(EnumChatFormatting.GREEN + "Player stats checking is now "
                + (AutoSniper.config.playerCheckEnabled
                   ? EnumChatFormatting.AQUA + "enabled"
                   : EnumChatFormatting.RED + "disabled"));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
