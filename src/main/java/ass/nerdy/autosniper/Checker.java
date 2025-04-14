package ass.nerdy.autosniper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    public Checker() {
        MinecraftForge.EVENT_BUS.register(this);
    }

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
        return mc.thePlayer != null && mc.theWorld != null;
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (!isPlayerInGame()) return;
        String msg = event.message.getUnformattedText();

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

            Collection<NetworkPlayerInfo> playerInfoCollection = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
            Collection<NetworkPlayerInfo> safePlayerInfoCollection = new CopyOnWriteArrayList<>(playerInfoCollection);
            List<String> blacklistedUsers = AutoSniper.config.getBlacklistedUsers();

            ExecutorService executor = Executors.newFixedThreadPool(8); // 8 to slightly help with ratelimit
            List<Future<Boolean>> futures = new CopyOnWriteArrayList<>();

            for (NetworkPlayerInfo playerInfo : safePlayerInfoCollection) {
                futures.add(executor.submit(() -> {
                    String uuid = playerInfo.getGameProfile().getId().toString();
                    String ign = playerInfo.getGameProfile().getName();
                    String tabDisplayName = getFormattedDisplayName(ign);

                    if ("NONE".equals(tabDisplayName)) return false;

                    if (blacklistedUsers.contains(ign)) {
                        playSound("random.levelup", 1.0f, 1.0f);
                        AutoSniper.log(EnumChatFormatting.RED + tabDisplayName + EnumChatFormatting.RED + " is BLACKLISTED!");
                        return true;
                    }

                    PlayerData playerData = getPlayerData(uuid);
                    if (playerData != null) {
                        double fkdr = playerData.getFkdr();
                        if (fkdr >= getMinFKDR()) {
                            playSound("note.pling", 1.0f, 1.0f);
                            AutoSniper.log(
                                    EnumChatFormatting.DARK_PURPLE + tabDisplayName + EnumChatFormatting.AQUA + " has FKDR: " + EnumChatFormatting.DARK_PURPLE + fkdr);
                            return true;
                        }
                    } else {
                        System.out.println(ign + " is NOT a valid Hypixel player.");
                        playSound("random.levelup", 1.0f, 1.0f);
                        AutoSniper.log(EnumChatFormatting.LIGHT_PURPLE + tabDisplayName + EnumChatFormatting.LIGHT_PURPLE + " is nicked!");
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
                mc.thePlayer.sendChatMessage(AutoSniper.config.autoRqCommand);
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
                AutoSniper.log(EnumChatFormatting.RED + "Error, Key Not Set!");
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

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!overlay) return;

        FontRenderer font = mc.fontRendererObj;

        int x = 10;
        int y = 10;
        int lineSpacing = 10;
        int color = 0xFFFFFF;

        String autoRqStatus = "auto-rq: " + (isAutoRqEnabled() ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled");
        font.drawStringWithShadow(autoRqStatus, x, y, color);

        if (isAutoRqEnabled()) {
            y += lineSpacing;
            String autoRqCommandText = "cmd: " + EnumChatFormatting.AQUA + AutoSniper.config.autoRqCommand;
            font.drawStringWithShadow(autoRqCommandText, x, y, color);
        }

        y += lineSpacing;
        String snipeCommand =
                "stats-check: " + (AutoSniper.config.playerCheckEnabled ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled");
        font.drawStringWithShadow(snipeCommand, x, y, color);
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
