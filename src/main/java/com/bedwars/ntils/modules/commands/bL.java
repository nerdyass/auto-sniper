package com.bedwars.ntils.modules.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class bL {
    private final String prefix = Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + "N" + Formatting.GRAY + "] ";

    public bL() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("target")
                .then(ClientCommandManager.argument("username", StringArgumentType.word())
                        .executes(ctx -> {
                            String username = StringArgumentType.getString(ctx, "username");
                            File configDir = getConfigDirectory();
                            File blacklistFile = new File(configDir, "blacklist.txt");
                            saveUser(blacklistFile, username, ctx.getSource());
                            return 1;
                        }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.literal(prefix + Formatting.RED + "Usage: /target <username>"));
                    return 0;
                })
        );
    }

    private File getConfigDirectory() {
        String appData = System.getenv("APPDATA");
        if (appData != null) {
            return new File(appData, ".minecraft/config");
        } else {
            String userHome = System.getProperty("user.home");
            return new File(userHome, ".minecraft/config");
        }
    }

    private void saveUser(File file, String username, FabricClientCommandSource source) {
        FileWriter writer = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            writer = new FileWriter(file, true);
            writer.write(username + "\n");

            source.sendFeedback(Text.literal(
                            prefix + Formatting.GREEN + "User " + Formatting.RED + username + Formatting.GREEN + " has been added to targets!"));
        } catch (IOException e) {
            source.sendFeedback(
                    Text.literal(prefix + Formatting.RED + "Failed to blacklist user: " + e.getMessage()));
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    source.sendFeedback(
                            Text.literal(prefix + Formatting.RED + "Error closing file writer: " + e.getMessage()));
                }
            }
        }
    }
}
