package com.bedwars.ntils.modules.commands;

import com.bedwars.ntils.modules.pC;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class tPC {
    final String prefix = Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + "N" + Formatting.GRAY + "] ";

    private final pC checker;

    public tPC(pC checker) {
        this.checker = checker;

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("snipe")
                .executes(context -> {
                    checker.tPC();
                    boolean currentStatus = checker.isPCenabled();
                    updatePCC(currentStatus);

                    context.getSource().sendFeedback(
                            Text.literal(
                                    prefix + Formatting.GREEN + "Player stats checking is now " + (currentStatus
                                            ? Formatting.AQUA + "enabled"
                                            : Formatting.RED + "disabled") + "."));

                    return 1;
                })
        );
    }

    private void updatePCC(boolean enabled) {
        File configFile = new File("config/autosniper.json");

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reader);
                JsonObject existingConfig = element.getAsJsonObject();

                existingConfig.addProperty("playerCheckEnabled", enabled);

                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(existingConfig.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
