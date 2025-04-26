package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        try {
            URL url = new URL("https://api.hypixel.net/player?key=" + apiKey + "&uuid=00000000-0000-0000-0000-000000000000");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                response.append((char) c);
            }

            String jsonResponse = response.toString();
            if (jsonResponse.contains("\"success\":true")) {
                AutoSniper.log(EnumChatFormatting.GREEN + "API key is valid!");
                AutoSniper.config.apiKey = apiKey;
                if (AutoSniper.config.save()) {
                    AutoSniper.log(EnumChatFormatting.LIGHT_PURPLE + apiKeyName + EnumChatFormatting.GREEN + " saved successfully!");
                } else {
                    AutoSniper.log(EnumChatFormatting.RED + "Failed to save " + apiKeyName);
                }
            } else {
                AutoSniper.log(EnumChatFormatting.RED + "API key is invalid!");
            }

        } catch (IOException e) {
            AutoSniper.log(EnumChatFormatting.RED + "API key is invalid!");
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
