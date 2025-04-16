package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

public class TargetsCmd extends CommandBase {
    @Override
    public String getCommandName() {
        return "targets";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/targets";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        listBlacklistedUsers();
    }

    private void listBlacklistedUsers() {
        File file = new File("config/blacklist.json");
        if (!file.exists()) {
            AutoSniper.log(EnumChatFormatting.RED + "No blacklisted users found.");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            JsonObject obj = new JsonParser().parse(builder.toString()).getAsJsonObject();

            if (obj.entrySet().isEmpty()) {
                AutoSniper.log(EnumChatFormatting.RED + "No blacklisted users found.");
                return;
            }

            AutoSniper.log(EnumChatFormatting.GREEN + "Blacklisted users:");
            for (Map.Entry<String, com.google.gson.JsonElement> entry : obj.entrySet()) {
                String uuid = entry.getKey();
                String name = entry.getValue().getAsString();
                AutoSniper.log(EnumChatFormatting.RED + "UUID: " + uuid + " | Username: " + name);
            }
        } catch (IOException e) {
            AutoSniper.log(EnumChatFormatting.RED + "Failed to read blacklist file: " + e.getMessage());
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
