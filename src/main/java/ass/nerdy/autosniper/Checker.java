package ass.nerdy.autosniper;

import ass.nerdy.autosniper.event.events.HudRenderEvent;
import ass.nerdy.autosniper.event.events.ReceiveMessageEvent;
import ass.nerdy.autosniper.orbit.EventHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

import static ass.nerdy.autosniper.AutoSniper.mc;
import static ass.nerdy.autosniper.Util.*;

public class Checker {
    private boolean inBwGame;
    private boolean overlay = true;

    private static String fetchPlayerData(String uuid, String currentApiKey) throws IOException {
        URL url = new URL("https://api.hypixel.net/player?key=" + currentApiKey + "&uuid=" + uuid);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        StringBuilder response = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            response.append((char) c);
        }

        return response.toString();
    }

    public boolean isPlayerInGame() {
        return mc.player != null && mc.world != null;
    }

    @EventHandler
    public void onChatMessageReceived(ReceiveMessageEvent event) {
        if (!isPlayerInGame()) return;
        String msg = event.getMessage().getLiteralString();
        if (msg == null) return;

        if (msg.contains("1st Killer") || msg.contains("joined the lobby") || msg.contains("has joined (")) {
            inBwGame = false;
        } else if (msg.contains("Protect your bed and destroy the enemy beds.") || msg.contains("Players swap teams at random")) {
            inBwGame = true;
            checkRequest();
        }
    }

    private void checkRequest() {
        if (!AutoSniper.config.playerCheckEnabled || !inBwGame) return;

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
            Collection<PlayerListEntry> playerInfoCollection = networkHandler.getPlayerList();
            Collection<PlayerListEntry> safePlayerInfoCollection = new CopyOnWriteArrayList<>(playerInfoCollection);
            Map<String, String> blacklist = AutoSniper.config.getBlacklistedUsers();

            ExecutorService executor = Executors.newFixedThreadPool(8);
            List<Future<Boolean>> futures = new CopyOnWriteArrayList<>();

            for (PlayerListEntry playerInfo : safePlayerInfoCollection) {
                futures.add(executor.submit(() -> {
                    String uuid = playerInfo.getProfile().getId().toString();
                    String uuidWithoutDashes = uuid.replace("-", "");
                    String ign = playerInfo.getProfile().getName();
                    String tabDisplayName = getFormattedDisplayName(ign);

                    if ("NONE".equals(tabDisplayName)) return false;

                    if (isBlacklisted(uuidWithoutDashes, ign, blacklist)) {
                        playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        AutoSniper.log(Formatting.RED + tabDisplayName + Formatting.RED + " is BLACKLISTED!");
                        return true;
                    }

                    PlayerData playerData = getPlayerData(uuid);
                    if (playerData != null) {
                        double fkdr = playerData.getFkdr();
                        if (fkdr >= getMinFKDR()) {
                            playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.BLOCKS, 1.0f, 1.0f);
                            AutoSniper.log(
                                    Formatting.DARK_PURPLE + tabDisplayName + Formatting.AQUA + " has FKDR: " + Formatting.DARK_PURPLE + fkdr);
                            return true;
                        }
                    } else {
                        System.out.println(ign + " is NOT a valid Hypixel player.");
                        playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        AutoSniper.log(Formatting.LIGHT_PURPLE + tabDisplayName + Formatting.LIGHT_PURPLE + " is nicked!");
                        return true;
                    }
                    return false;
                }));
            }

            executor.shutdown();
            boolean anyValidPlayer = false;
            for (Future<Boolean> future : futures) {
                try {
                    if (future.get()) {
                        anyValidPlayer = true;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            inBwGame = false;
            AutoSniper.log("§aChecks completed");

            if (!anyValidPlayer && AutoSniper.config.autoRqEnabled && !AutoSniper.config.autoRqCommand.isEmpty()) {
                mc.getNetworkHandler().sendCommand(AutoSniper.config.autoRqCommand);
                AutoSniper.log("§aAuto-RQ executed: " + AutoSniper.config.autoRqCommand);
            }
        }).start();
    }

    public double getMinFKDR() {
        return AutoSniper.config.fkdrValue;
    }

    public boolean isAutoRqEnabled() {
        return AutoSniper.config.autoRqEnabled;
    }

    public PlayerData getPlayerData(String uuid) {
        try {
            String currentApiKey = AutoSniper.config.apiKey;
            if (currentApiKey == null || currentApiKey.isEmpty()) {
                AutoSniper.log(Formatting.RED + "Error, Key Not Set!");
                return null;
            }
            String responseBody = fetchPlayerData(uuid, currentApiKey);

            if (!responseBody.contains("\"success\":true")) {
                if (responseBody.contains("Invalid API key")) {
                    AutoSniper.log(Formatting.RED + "Warning: Your Hypixel API key is invalid.");
                } else if (responseBody.contains("key throttle")) {
                    AutoSniper.log(Formatting.RED + "Warning: Your Hypixel API key is being rate-limited (too many requests).");
                } else {
                    AutoSniper.log(Formatting.RED + "Warning: Failed to fetch data. Check your API key.");
                }
                return null;
            }

            if (responseBody.contains("\"player\":null")) {
                return null;
            }

            String killsStr = extractJsonValue(responseBody, "final_kills_bedwars");
            String deathsStr = extractJsonValue(responseBody, "final_deaths_bedwars");

            int finalKills = parseIntSafely(killsStr);
            int finalDeaths = parseIntSafely(deathsStr);

            double fkdr = finalDeaths == 0 ? finalKills : (double) finalKills / finalDeaths;
            return new PlayerData(Math.round(fkdr * 10.0) / 10.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String extractJsonValue(String json, String key) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            JsonElement value = obj.get(key);
            if (value.isJsonPrimitive()) {
                JsonPrimitive prim = value.getAsJsonPrimitive();
                if (prim.isString()) return prim.getAsString();
                if (prim.isNumber()) return prim.getAsNumber().toString();
                if (prim.isBoolean()) return String.valueOf(prim.getAsBoolean());
            }
        } catch (Exception e) {
            AutoSniper.log(Formatting.RED + "Failed to parse JSON: " + e.getMessage());
        }
        return null;
    }

    private boolean isBlacklisted(String uuid, String username, Map<String, String> blacklist) {
        return blacklist.containsKey(uuid) || blacklist.containsKey(username);
    }

    public void togglePlayerCheckEnabled() {
        AutoSniper.config.playerCheckEnabled = !AutoSniper.config.playerCheckEnabled;
    }

    @EventHandler
    public void onRenderOverlay(HudRenderEvent event) {
        if (!overlay) return;

        DrawContext context = event.drawContext;

        int x = 10;
        int y = 10;
        int lineSpacing = 10;
        int color = 0xFFFFFF;

        String autoRqStatus = "auto-rq: " + (isAutoRqEnabled() ? Formatting.GREEN + "enabled" : Formatting.RED + "disabled");
        context.drawTextWithShadow(mc.textRenderer, autoRqStatus, x, y, color);

        if (isAutoRqEnabled()) {
            y += lineSpacing;
            String autoRqCommandText = "cmd: " + Formatting.AQUA + AutoSniper.config.autoRqCommand;
            context.drawTextWithShadow(mc.textRenderer, autoRqCommandText, x, y, color);
        }

        y += lineSpacing;
        String snipeCommand =
                "stats-check: " + (AutoSniper.config.playerCheckEnabled ? Formatting.GREEN + "enabled" : Formatting.RED + "disabled");
        context.drawTextWithShadow(mc.textRenderer, snipeCommand, x, y, color);
    }

    public void toggleOverlay() {
        overlay = !overlay;
    }

    public boolean isOverlayVisible() {
        return overlay;
    }

    public static class PlayerData {
        private final double fkdr;

        public PlayerData(double fkdr) {
            this.fkdr = fkdr;
        }

        public double getFkdr() {
            return fkdr;
        }
    }
}
