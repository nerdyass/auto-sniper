package ass.nerdy.autosniper.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class cF {
    private static final String CONFIG_FILE_PATH = "config/autosniper.json";

    public static void InitConfig() {
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
                JsonObject eC = element.getAsJsonObject();

                if (!eC.has("autoRqEnabled")) {
                    eC.addProperty("autoRqEnabled", false);
                }
                if (!eC.has("playerCheckEnabled")) {
                    eC.addProperty("playerCheckEnabled", false);
                }
                if (!eC.has("hudVisible")) {
                    eC.addProperty("hudVisible", false);
                }
                if (!eC.has("fkdrValue")) {
                    eC.addProperty("fkdrValue", 20);
                }
                if (!eC.has("autoRqCommand")) {
                    eC.addProperty("autoRqCommand", "");
                }
                if (!eC.has("apiKey")) {
                    eC.addProperty("apiKey", "");
                }

                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(eC.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
