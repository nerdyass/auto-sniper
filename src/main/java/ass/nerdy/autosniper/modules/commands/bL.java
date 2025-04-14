package ass.nerdy.autosniper.modules.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class bL extends CommandBase {
    private final String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "N" + EnumChatFormatting.GRAY + "] ";

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
            sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "Usage: /target <username>"));
            return;
        }

        String username = args[0];
        File configDir = configDirec();
        File blacklistFile = new File(configDir, "blacklist.txt");

        saveUser(blacklistFile, username, sender);
    }

    //I'm very aware this whole blacklist system is OUTDATED to shit, will be on the priority list for updating.

    private File configDirec() {
        String appData = System.getenv("APPDATA");
        if (appData != null) {
            return new File(appData, ".minecraft/config");
        } else {
            String userHome = System.getProperty("user.home");
            return new File(userHome, ".minecraft/config");
        }
    }

    private void saveUser(File file, String username, ICommandSender sender) {
        FileWriter writer = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            writer = new FileWriter(file, true);
            writer.write(username + "\n");

            sender.addChatMessage(new ChatComponentText(
                    prefix + EnumChatFormatting.GREEN + "User " + EnumChatFormatting.RED + username + EnumChatFormatting.GREEN + " has been added to targets!"));
        } catch (IOException e) {
            sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "Failed to blacklist user: " + e.getMessage()));
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "Error closing file writer: " + e.getMessage()));
                }
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
