package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TargetCmd extends CommandBase {
    @Override
    public String getCommandName() {
        return "target";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/target <username>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            AutoSniper.log(EnumChatFormatting.RED + "Usage: /target <username>");
            return;
        }

        String username = args[0];
        File blacklistFile = new File("config/blacklist.txt");

        saveUser(blacklistFile, username);
    }

    // I'm very aware this whole blacklist system is OUTDATED to shit, will be on the priority list for updating.

    private void saveUser(File file, String username) {
        FileWriter writer = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            writer = new FileWriter(file, true);
            writer.write(username + "\n");

            AutoSniper.log(
                    EnumChatFormatting.GREEN + "User " + EnumChatFormatting.RED + username + EnumChatFormatting.GREEN + " has been added to targets!");
        } catch (IOException e) {
            AutoSniper.log(EnumChatFormatting.RED + "Failed to blacklist user: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    AutoSniper.log(EnumChatFormatting.RED + "Error closing file writer: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
