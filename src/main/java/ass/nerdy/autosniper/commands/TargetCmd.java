package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TargetCmd {

    public TargetCmd() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("target")
                .then(ClientCommandManager.argument("username", StringArgumentType.word())
                        .executes(ctx -> {
                            String username = StringArgumentType.getString(ctx, "username");
                            File blacklistFile = new File("config/blacklist.txt");

                            saveUser(blacklistFile, username);
                            return 1;
                        }))
                .executes(ctx -> {
                    AutoSniper.log(Formatting.RED + "Usage: /target <username>");
                    return 0;
                })
        );
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
                    Formatting.GREEN + "User " + Formatting.RED + username + Formatting.GREEN + " has been added to targets!");
        } catch (IOException e) {
            AutoSniper.log(Formatting.RED + "Failed to blacklist user: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    AutoSniper.log(Formatting.RED + "Error closing file writer: " + e.getMessage());
                }
            }
        }
    }
}