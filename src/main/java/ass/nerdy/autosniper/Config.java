package ass.nerdy.autosniper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    public boolean autoRqEnabled;
    public boolean playerCheckEnabled;
    public boolean hudVisible;
    public double fkdrValue;
    public String autoRqCommand;
    public String apiKey;

    private File getFile() {
        return new File("config/autosniper.json");
    }

    public void load() {
        File file = getFile();
        if (!file.exists()) {
            load(new JsonObject());
            save();
            return;
        }
        if (!file.isFile()) {
            load(new JsonObject());
            // File exists but is not a file
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            load(new JsonParser().parse(reader)
                    .getAsJsonObject());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void load(JsonObject cfg) {
        autoRqEnabled = getSafe(cfg, "autoRqEnabled", false);
        playerCheckEnabled = getSafe(cfg, "playerCheckEnabled", false);
        hudVisible = getSafe(cfg, "hudVisible", false);
        fkdrValue = getSafe(cfg, "fkdrValue", 20);
        autoRqCommand = getSafe(cfg, "autoRqCommand", "");
        apiKey = getSafe(cfg, "apiKey", "");
    }

    public boolean save() {
        JsonObject cfg = new JsonObject();
        cfg.addProperty("autoRqEnabled", autoRqEnabled);
        cfg.addProperty("playerCheckEnabled", playerCheckEnabled);
        cfg.addProperty("hudVisible", hudVisible);
        cfg.addProperty("fkdrValue", fkdrValue);
        cfg.addProperty("autoRqCommand", autoRqCommand);
        cfg.addProperty("apiKey", apiKey);

        try (FileWriter writer = new FileWriter(getFile())) {
            writer.write(cfg.toString());
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static boolean getSafe(JsonObject obj, String key, boolean defaultValue) {
        if (obj.has(key)) obj.get(key).getAsBoolean();
        return defaultValue;
    }

    private static int getSafe(JsonObject obj, String key, int defaultValue) {
        if (obj.has(key)) obj.get(key).getAsInt();
        return defaultValue;
    }

    private static String getSafe(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key)) obj.get(key).getAsString();
        return defaultValue;
    }

    public boolean setFKDRValue(String str) {
        try {
            AutoSniper.config.fkdrValue = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException ex) {
            AutoSniper.log(Formatting.RED + "Invalid FKDR value! Please enter a valid number.");
            return false;
        }
    }

    public Map<String, String> getBlacklistedUsers() {
        File blacklistFile = new File("config/blacklist.json");
        Map<String, String> blacklist = new HashMap<>();

        if (blacklistFile.exists() && blacklistFile.isFile()) {
            try {
                String json = Files.readString(blacklistFile.toPath(), StandardCharsets.UTF_8);
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

                for (String key : obj.keySet()) {
                    blacklist.put(key, obj.get(key).getAsString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return blacklist;
    }
}