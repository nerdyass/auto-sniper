package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Scanner;

public class TargetCmd extends CommandBase {
    @Override
    public String getCommandName() {
        return "target";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/target <username>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            AutoSniper.log(EnumChatFormatting.RED + "Usage: /target <username>");
            return;
        }

        String input = args[0];
        String uuid;
        String username;

        if (isValidUUID(input)) {
            uuid = normalizeUUID(input);
            username = resolveUsername(uuid);
        } else {
            uuid = resolveUUID(input);
            if (uuid == null) {
                AutoSniper.log(EnumChatFormatting.RED + "Failed to resolve UUID for user: " + input);
                return;
            }
            username = input;
        }

        saveUser(uuid, username);
    }

    private void saveUser(String uuid, String username) {
        try {
            File file = new File("config/blacklist.json");
            file.getParentFile().mkdirs();

            JsonObject obj;
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                String content = builder.toString();
                obj = content.isEmpty() ? new JsonObject() : new JsonParser().parse(content).getAsJsonObject();
            } else {
                obj = new JsonObject();
            }

            obj.addProperty(uuid, username);

            FileWriter writer = new FileWriter(file);
            writer.write(obj.toString());
            writer.close();

            AutoSniper.log(EnumChatFormatting.GREEN + "User " + EnumChatFormatting.RED + username + EnumChatFormatting.GREEN + " has been added to targets!");
        } catch (IOException e) {
            AutoSniper.log(EnumChatFormatting.RED + "Failed to blacklist user: " + e.getMessage());
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
            String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
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
            String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
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

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
