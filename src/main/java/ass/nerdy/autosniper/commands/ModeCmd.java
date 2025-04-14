package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

public class ModeCmd extends CommandBase {
    private final Map<String, String> modeMap = new HashMap<>();

    public ModeCmd() {
        modeMap.put("1s", "/play bedwars_eight_one");
        modeMap.put("2s", "/play bedwars_eight_two");
        modeMap.put("3s", "/play bedwars_four_three");
        modeMap.put("4s", "/play bedwars_four_four");
        modeMap.put("4v4", "/play bedwars_two_four");
    }

    @Override
    public String getCommandName() {
        return "mode";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mode [mode]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            AutoSniper.log(EnumChatFormatting.RED + "You must provide a mode to set!");
            return;
        }

        String input = args[0].toLowerCase();
        String command = modeMap.getOrDefault(input, String.join(" ", args));

        AutoSniper.config.autoRqCommand = command;
        AutoSniper.config.save();

        AutoSniper.log(EnumChatFormatting.GREEN + "Auto-RQ mode set to: " + EnumChatFormatting.AQUA + command);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
