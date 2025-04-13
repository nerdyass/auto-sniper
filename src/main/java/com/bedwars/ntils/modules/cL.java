package com.bedwars.ntils.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class cL {
    private static final String CONFIG_FILE_PATH = "config/autosniper.json";
    private JsonObject cJ;

    public cL() {
        cJ = new JsonObject();
        initializeConfig();
    }

    private void initializeConfig() {
        File configFile = new File(CONFIG_FILE_PATH);

        if (!configFile.exists()) {
            JsonObject dC = new JsonObject();
            dC.addProperty("autoRqEnabled", false);
            dC.addProperty("playerCheckEnabled", false);
            dC.addProperty("hudVisible", false);
            dC.addProperty("fkdrValue", 20);
            dC.addProperty("autoRqCommand", "");
            dC.addProperty("apiKey", "");

            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(dC.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reader);
                cJ = element.getAsJsonObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean gB(String key, boolean dV) {
        return cJ.has(key) ? cJ.get(key).getAsBoolean() : dV;
    }

    public String gS(String key, String sdV) {
        return cJ.has(key) ? cJ.get(key).getAsString() : sdV;
    }

    public double gD(String key, double ddV) {
        return cJ.has(key) ? cJ.get(key).getAsDouble() : ddV;
    }

    public void saveConfig() {
        File configFile = new File(CONFIG_FILE_PATH);
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(cJ.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
