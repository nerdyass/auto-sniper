package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import ass.nerdy.autosniper.Checker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class HUDCmd extends CommandBase {
    private final Checker checker;

    public HUDCmd(Checker checker) {
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
        checker.toggleOverlay();
        boolean currentStatus = checker.isOverlayVisible();
        updateHudConfig(currentStatus);
        AutoSniper.log(EnumChatFormatting.GREEN + "HUD is now "
                + (currentStatus
                   ? EnumChatFormatting.AQUA + "visible"
                   : EnumChatFormatting.RED + "hidden")
        );
    }

    private void updateHudConfig(boolean visible) {
        AutoSniper.config.hudVisible = visible;
        AutoSniper.config.save();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
