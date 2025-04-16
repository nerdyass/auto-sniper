package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TargetCmd {

    public TargetCmd() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("target")
                .then(ClientCommandManager.argument("input", StringArgumentType.word())
                        .executes(ctx -> {
                            String input = StringArgumentType.getString(ctx, "input");
                            String uuid;
                            String username;

                            if (isValidUUID(input)) {
                                uuid = normalizeUUID(input);
                                username = resolveUsername(uuid);
                            } else {
                                uuid = resolveUUID(input);
                                if (uuid == null) {
                                    AutoSniper.log(Formatting.RED + "Failed to resolve UUID for user: " + input);
                                    return 0;
                                }
                                username = input;
                            }

                            saveUser(uuid, username);
                            return 1;
                        }))
                .executes(ctx -> {
                    AutoSniper.log(Formatting.RED + "Usage: /target <username|uuid>");
                    return 0;
                })
        );

        dispatcher.register(ClientCommandManager.literal("targets")
                .executes(ctx -> {
                    listBlacklistedUsers();
                    return 1;
                })
        );
    }

    private void saveUser(String uuid, String username) {
        try {
            File file = new File("config/blacklist.json");
            file.getParentFile().mkdirs();

            JsonObject obj;
            if (file.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                obj = content.isEmpty() ? new JsonObject() : JsonParser.parseString(content).getAsJsonObject();
            } else {
                obj = new JsonObject();
            }

            obj.addProperty(uuid, username);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(obj.toString());
            }

            AutoSniper.log(Formatting.GREEN + "User " + Formatting.RED + username + Formatting.GREEN + " has been added to targets!");
        } catch (IOException e) {
            AutoSniper.log(Formatting.RED + "Failed to blacklist user: " + e.getMessage());
        }
    }

    private void listBlacklistedUsers() {
        File file = new File("config/blacklist.json");
        if (!file.exists()) {
            AutoSniper.log(Formatting.RED + "No blacklisted users found.");
            return;
        }

        try {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            JsonObject obj = JsonParser.parseString(content).getAsJsonObject();

            if (obj.size() == 0) {
                AutoSniper.log(Formatting.RED + "No blacklisted users found.");
                return;
            }

            AutoSniper.log(Formatting.GREEN + "Blacklisted users:");
            obj.entrySet().forEach(entry -> {
                String uuid = entry.getKey();
                String username = entry.getValue().getAsString();
                AutoSniper.log(Formatting.RED + "UUID: " + uuid + " | Username: " + username);
            });
        } catch (IOException e) {
            AutoSniper.log(Formatting.RED + "Failed to read blacklist file: " + e.getMessage());
        }
    }

    private String resolveUUID(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            String jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();

            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return json.get("id").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveUsername(String uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", ""));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            String jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();

            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return json.get("name").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isValidUUID(String str) {
        return str.matches("^[0-9a-fA-F]{32}$") || str.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    private String normalizeUUID(String uuid) {
        return uuid.replace("-", "").toLowerCase();
    }
}
