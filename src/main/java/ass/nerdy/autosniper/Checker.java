package ass.nerdy.autosniper;

import ass.nerdy.autosniper.event.events.HudRenderEvent;
import ass.nerdy.autosniper.event.events.ReceiveMessageEvent;
import ass.nerdy.autosniper.orbit.EventHandler;
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static ass.nerdy.autosniper.AutoSniper.mc;
import static ass.nerdy.autosniper.Util.*;

public class Checker {
    private boolean inBwGame;
    private boolean overlay = true;

    private static String fetchPlayerData(String uuid, String currentApiKey) throws IOException {
        URL url = new URL("https://api.hypixel.net/player?key=" + currentApiKey + "&uuid=" + uuid);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        StringBuilder response = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            response.append((char)c);
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
            List<String> blacklistedUsers = AutoSniper.config.getBlacklistedUsers();

            ExecutorService executor = Executors.newFixedThreadPool(8); // 8 to slightly help with ratelimit
            List<Future<Boolean>> futures = new CopyOnWriteArrayList<>();

            for (PlayerListEntry playerInfo : safePlayerInfoCollection) {
                futures.add(executor.submit(() -> {
                    String uuid = playerInfo.getProfile().getId().toString();
                    String ign = playerInfo.getProfile().getName();
                    String tabDisplayName = getFormattedDisplayName(ign);

                    if ("NONE".equals(tabDisplayName)) return false;

                    if (blacklistedUsers.contains(ign)) {
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

            System.out.println("Auto-RQ enabled, Command: " + AutoSniper.config.autoRqCommand);
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

            if (!responseBody.contains("\"success\":true") || responseBody.contains("\"player\":null")) {
                return null;
            }

            String killsStr = extractJsonValue(responseBody, "final_kills_bedwars");
            String deathsStr = extractJsonValue(responseBody, "final_deaths_bedwars");

            int finalKills = parseIntSafely(killsStr);
            int finalDeaths = parseIntSafely(deathsStr);

            double fkdr;
            if (finalDeaths == 0) {
                if (finalKills == 0) {
                    fkdr = 0.0;
                } else {
                    fkdr = finalKills;
                }
            } else {
                fkdr = (double)finalKills / finalDeaths;
            }

            fkdr = Math.round(fkdr * 10.0) / 10.0;

            return new PlayerData(fkdr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // this is horrid but i was learning Json Java when i made it and i cba to update it.
    public String extractJsonValue(String json, String key) {
        int startIdx = json.indexOf("\"" + key + "\":") + key.length() + 3;
        int endIdx = json.indexOf(",", startIdx);
        if (endIdx == -1) {
            endIdx = json.indexOf("}", startIdx);
        }
        return json.substring(startIdx, endIdx).trim();
    }


    public void togglePlayerCheckEnabled() {
        AutoSniper.config.playerCheckEnabled = !AutoSniper.config.playerCheckEnabled;
    }

    @EventHandler
    public void onRenderOverlay(HudRenderEvent event) {
        if (!overlay) return;

        // FontRenderer font = mc.fontRendererObj;
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