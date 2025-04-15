package com.bedwars.ntils.modules.commands;

import com.google.gson.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class aK {
    private final String prefix = Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + "N" + Formatting.GRAY + "] ";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public aK() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("key")
                .then(ClientCommandManager.argument("apikey", StringArgumentType.string())
                        .executes(ctx -> {
                            String apiKey = StringArgumentType.getString(ctx, "apikey");
                            File jsonFile = new File("config/autosniper.json");

                            JsonAPI(jsonFile, apiKey, ctx.getSource(), "Hypixel API key");
                            return 1;
                        }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.literal(prefix + Formatting.RED + "Usage: /key <apikey>"));
                    return 0;
                })
        );
    }

    private void JsonAPI(File file, String apiKey, FabricClientCommandSource sender, String apiKeyName) {
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

            sender.sendFeedback(Text.literal(prefix + Formatting.LIGHT_PURPLE + apiKeyName + Formatting.GREEN + " saved successfully!"));
        } catch (IOException e) {
            sender.sendFeedback(Text.literal(prefix + Formatting.RED + "Failed to save " + apiKeyName + ": " + e.getMessage()));
        }
    }
}
