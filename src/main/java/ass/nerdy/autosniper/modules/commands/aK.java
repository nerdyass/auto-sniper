package ass.nerdy.autosniper.modules.commands;

import com.google.gson.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class aK extends CommandBase {
    private final String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "N" + EnumChatFormatting.GRAY + "] ";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
            sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "Usage: /key <apikey>"));
            return;
        }

        String apiKey = args[0];
        File jsonFile = new File("config/autosniper.json");

        JsonAPI(jsonFile, apiKey, sender, "Hypixel API key");
    }

    private void JsonAPI(File file, String apiKey, ICommandSender sender, String apiKeyName) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            JsonObject config;

            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(reader);
                    config = element.getAsJsonObject();
                }
            } else {
                config = new JsonObject();
            }

            config.addProperty("apiKey", apiKey);

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(config, writer);
            }

            sender.addChatMessage(
                    new ChatComponentText(prefix + EnumChatFormatting.LIGHT_PURPLE + apiKeyName + EnumChatFormatting.GREEN + " saved successfully!"));
        } catch (IOException e) {
            sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "Failed to save " + apiKeyName + ": " + e.getMessage()));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
