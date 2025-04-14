package ass.nerdy.autosniper.modules.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class kC {
    private static final File CONFIG_DIR = getMinecraftConfigDirectory();
    private static final File HYPIXEL_API_KEY_FILE = new File(CONFIG_DIR, "autosniper.json");

    public static String getHypixelApiKey() {
        return readApiKeyFromJsonFile(HYPIXEL_API_KEY_FILE);
    }

    private static String readApiKeyFromJsonFile(File file) {
        if (!file.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            if (jsonObject != null && jsonObject.has("apiKey")) {
                return jsonObject.get("apiKey").getAsString();
            } else {
                System.err.println("Invalid JSON format: 'apiKey' not found.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getMinecraftConfigDirectory() {
        String appData = System.getenv("APPDATA");
        if (appData != null) {
            return new File(appData, ".minecraft/config");
        } else {
            String userHome = System.getProperty("user.home");
            return new File(userHome, ".minecraft/config");
        }
    }
}
