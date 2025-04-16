package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

                            try {
                                URL url = new URL("https://api.hypixel.net/key?key=" + apiKey);
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
                                    AutoSniper.log(Formatting.GREEN + "API key is valid!");
                                    AutoSniper.config.apiKey = apiKey;
                                    if (AutoSniper.config.save()) {
                                        AutoSniper.log(Formatting.LIGHT_PURPLE + apiKeyName + Formatting.GREEN + " saved successfully!");
                                    } else {
                                        AutoSniper.log(Formatting.RED + "Failed to save " + apiKeyName);
                                    }
                                } else {
                                    AutoSniper.log(Formatting.RED + "API key is invalid!");
                                }

                            } catch (IOException e) {
                                AutoSniper.log(Formatting.RED + "API key is invalid!");
                            }
                            return 1;
                        })
                )
                .executes(ctx -> {
                    AutoSniper.log(Formatting.RED + "Usage: /key <apikey>");
                    return 0;
                })
        );
    }
}