package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;

public class KeyCmd {

    public KeyCmd() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("key")
                .then(ClientCommandManager.argument("apikey", StringArgumentType.string())
                        .executes(ctx -> {
                            String apiKey = StringArgumentType.getString(ctx, "apikey");
                            String apiKeyName = "Hypixel API key";

                            AutoSniper.config.apiKey = apiKey;
                            if (AutoSniper.config.save()) {
                                AutoSniper.log(Formatting.LIGHT_PURPLE + apiKeyName + Formatting.GREEN + " saved successfully!");
                            } else {
                                AutoSniper.log(Formatting.RED + "Failed to save " + apiKeyName);
                            }
                            return 1;
                        }))
                .executes(ctx -> {
                    AutoSniper.log(Formatting.RED + "Usage: /key <apikey>");
                    return 0;
                })
        );
    }
}