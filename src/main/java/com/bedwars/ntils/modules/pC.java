package com.bedwars.ntils.modules;

import com.bedwars.ntils.event.events.HudRenderEvent;
import com.bedwars.ntils.event.events.ReceiveMessageEvent;
import com.bedwars.ntils.modules.commands.kC;
import com.bedwars.ntils.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class pC {
    private boolean playerCheckEnabled;
    private boolean autoRqEnabled;
    private String autoRqCommand;
    private double minFKDR;
    final String prefix = Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + "N" + Formatting.GRAY + "] ";
    MinecraftClient mc;

    boolean isBwGame = false;
    private cL configManager;

    public pC() {
        this.mc = MinecraftClient.getInstance();
        this.configManager = new cL();
        loadConfig();
    }

    private void loadConfig() {
        this.playerCheckEnabled = configManager.gB("playerCheckEnabled", false);
        this.autoRqEnabled = configManager.gB("autoRqEnabled", false);
        this.autoRqCommand = configManager.gS("autoRqCommand", "");
        this.minFKDR = configManager.gD("fkdrValue", 20.0);
    }

    public boolean isPlayerInGame() {
        return this.mc.player != null && this.mc.world != null;
    }

    @EventHandler
    public void onChatMessageReceived(ReceiveMessageEvent event) {
        String message = event.getMessage().getLiteralString();

        if (message != null && this.isPlayerInGame()) {
            if (message.contains("1st Killer")) {
                this.isBwGame = false;
            } else if (message.contains("Protect your bed and destroy the enemy beds.")) {
                this.isBwGame = true;
                checkRequest();
            } else if (message.contains("Players swap teams at random")) {
                this.isBwGame = true;
                checkRequest();
            } else if (message.contains("joined the lobby")) {
                this.isBwGame = false;
            } else if (message.contains("has joined (")) {
                this.isBwGame = false;
            }
        }
    }

    public void checkRequest() {
        if (!playerCheckEnabled) {
            return;
        }

        if (isBwGame) {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();

                Collection<PlayerListEntry> playerInfoCollection = networkHandler.getPlayerList();
                Collection<PlayerListEntry> safePlayerInfoCollection = new CopyOnWriteArrayList<>(playerInfoCollection);

                List<String> blacklistedUsers = getBlacklistedUsers();

                ExecutorService executor = Executors.newFixedThreadPool(8); // 8 to slightly help with ratelimit
                List<Future<Boolean>> futures = new CopyOnWriteArrayList<>();

                for (PlayerListEntry playerInfo : safePlayerInfoCollection) {
                    futures.add(executor.submit(() -> {
                        String uuid = playerInfo.getProfile().getId().toString();
                        String ign = playerInfo.getProfile().getName();
                        String tabDisplayName = gTBN(ign);

                        if ("NONE".equals(tabDisplayName)) return false;

                        if (blacklistedUsers.contains(ign)) {
                            playSound("random.levelup", 1.0f, 1.0f);
                            mc.player.sendMessage(Text.literal(prefix + Formatting.RED + tabDisplayName + Formatting.RED + " is BLACKLISTED!"));
                            return true;
                        }

                        PlayerData playerData = getPlayerData(uuid);
                        if (playerData != null) {
                            double fkdr = playerData.getFkdr();
                            if (fkdr >= getMinFKDR()) {
                                playSound("note.pling", 1.0f, 1.0f);
                                mc.player.sendMessage(Text.literal(prefix + Formatting.DARK_PURPLE + tabDisplayName + Formatting.AQUA + " has FKDR: " + Formatting.DARK_PURPLE + fkdr));
                                return true;
                            }
                        } else {
                            System.out.println(ign + " is NOT a valid Hypixel player.");
                            playSound("random.levelup", 1.0f, 1.0f);
                            mc.player.sendMessage(Text.literal(prefix + Formatting.LIGHT_PURPLE + tabDisplayName + Formatting.LIGHT_PURPLE + " is nicked!"));
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

                System.out.println("Auto-RQ enabled, Command: " + autoRqCommand);
                isBwGame = false;

                mc.player.sendMessage(Text.literal(prefix + Formatting.GREEN + "Checks completed."));
                if (!anyValidPlayer && autoRqEnabled && !autoRqCommand.isEmpty()) {
                    mc.getNetworkHandler().sendCommand(autoRqCommand);
                    mc.player.sendMessage(Text.literal(prefix + Formatting.GREEN + "Auto-RQ executed: " + autoRqCommand));
                }
            }).start();
        }
    }

    private List<String> getBlacklistedUsers() {
        File configDir = new File(System.getenv("APPDATA"), ".minecraft/config");
        File blacklistFile = new File(configDir, "blacklist.txt");

        if (!blacklistFile.exists()) {
            return Collections.emptyList();
        }

        try {
            return Files.readAllLines(Paths.get(blacklistFile.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public double getMinFKDR() {
        return minFKDR;
    }

    public void setFkdrValue(String fkdrValue) {
        try {
            minFKDR = Double.parseDouble(fkdrValue);
        } catch (NumberFormatException e) {
            mc.player.sendMessage(Text.literal(prefix + Formatting.RED + "Invalid FKDR value! Please enter a valid number."));
        }
    }

    public void setAutoRqEnabled(boolean enabled) {
        this.autoRqEnabled = enabled;
    }

    public void setAutoRqCommand(String command) {
        this.autoRqCommand = command;
    }

    public boolean isAutoRqEnabled() {
        return autoRqEnabled;
    }

    public String getAutoRqCommand() {
        return autoRqCommand;
    }

    public PlayerData getPlayerData(String uuid) {
        try {
            String currentApiKey = kC.getHypixelApiKey();
            if (currentApiKey == null || currentApiKey.isEmpty()) {
                mc.player.sendMessage(Text.literal(prefix + Formatting.RED + "Error, Key Not Set!"));
                return null;
            }
            URL url = new URL("https://api.hypixel.net/player?key=" + currentApiKey + "&uuid=" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                response.append((char) c);
            }

            String responseBody = response.toString();

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
                fkdr = (double) finalKills / finalDeaths;
            }

            fkdr = Math.round(fkdr * 10.0) / 10.0;

            return new PlayerData(fkdr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int parseIntSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    //this is horrid but i was learning Json Java when i made it and i cba to update it.

    public String extractJsonValue(String json, String key) {
        int startIdx = json.indexOf("\"" + key + "\":") + key.length() + 3;
        int endIdx = json.indexOf(",", startIdx);
        if (endIdx == -1) {
            endIdx = json.indexOf("}", startIdx);
        }
        return json.substring(startIdx, endIdx).trim();
    }

    public class PlayerData {
        private final double fkdr;

        public PlayerData(double fkdr) {
            this.fkdr = fkdr;
        }

        public double getFkdr() {
            return fkdr;
        }
    }

    private String gTBN(String playerName) {
        Team playerTeam = mc.world.getScoreboard().getScoreHolderTeam(playerName);
        if (playerTeam == null) {
            return "NONE";
        }
        int length = playerTeam.getColor().getName().length();
        if (length == 10) {
            return playerTeam.getPrefix() + playerName + playerTeam.getSuffix();
        }
        return "NONE";
    }

    public boolean isNoStats(PlayerData playerData) {
        return playerData == null || playerData.getFkdr() == 0.0;
    }

    private void playSound(String soundName, float volume, float pitch) {
        if (mc.player != null && mc.world != null) {
            if (soundName.contains("levelup")) {
                mc.world.playSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                        SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, volume, pitch, false);
            } else {
                mc.world.playSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                        SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.BLOCKS, volume, pitch, false);
            }
        }
    }

    public void tPC() {
        playerCheckEnabled = !playerCheckEnabled;
    }

    public boolean isPCenabled() {
        return playerCheckEnabled;
    }

    @EventHandler
    public void onRenderOverlay(HudRenderEvent event) {
        if (!hudVisible) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        // FontRenderer fontRenderer = mc.fontRendererObj;

        int x = 10;
        int y = 10;
        int lineSpacing = 10;
        int color = 0xFFFFFF;

        String autoRqStatus = "auto-rq: " + (isAutoRqEnabled() ? Formatting.GREEN + "enabled" : Formatting.RED + "disabled");
        event.drawContext.drawTextWithShadow(mc.textRenderer, autoRqStatus, x, y, color);

        if (isAutoRqEnabled()) {
            y += lineSpacing;
            String autoRqCommandText = "cmd: " + Formatting.AQUA + getAutoRqCommand();
            event.drawContext.drawTextWithShadow(mc.textRenderer, autoRqCommandText, x, y, color);
        }

        y += lineSpacing;
        String snipeCommand = "stats-check: " + (isPCenabled() ? Formatting.GREEN + "enabled" : Formatting.RED + "disabled");
        event.drawContext.drawTextWithShadow(mc.textRenderer, snipeCommand, x, y, color);
    }

    private boolean hudVisible = true;

    public void tHudVisibility() {
        this.hudVisible = !this.hudVisible;
    }

    public boolean isHudVisible() {
        return this.hudVisible;
    }
}


