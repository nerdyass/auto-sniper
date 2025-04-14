package ass.nerdy.autosniper.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    final String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "N" + EnumChatFormatting.GRAY + "] ";
    Minecraft mc;

    boolean isBwGame;
    private final cL configManager;

    public pC() {
        mc = Minecraft.getMinecraft();
        configManager = new cL();
        loadConfig();
    }

    private void loadConfig() {
        playerCheckEnabled = configManager.gB("playerCheckEnabled", false);
        autoRqEnabled = configManager.gB("autoRqEnabled", false);
        autoRqCommand = configManager.gS("autoRqCommand", "");
        minFKDR = configManager.gD("fkdrValue", 20.0);
    }

    public boolean isPlayerInGame() {
        return mc.thePlayer != null && mc.theWorld != null;
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (isPlayerInGame()) {
            if (message.contains("1st Killer")) {
                isBwGame = false;
            } else if (message.contains("Protect your bed and destroy the enemy beds.")) {
                isBwGame = true;
                checkRequest();
            } else if (message.contains("Players swap teams at random")) {
                isBwGame = true;
                checkRequest();
            } else if (message.contains("joined the lobby")) {
                isBwGame = false;
            } else if (message.contains("has joined (")) {
                isBwGame = false;
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

                Collection<NetworkPlayerInfo> playerInfoCollection = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
                Collection<NetworkPlayerInfo> safePlayerInfoCollection = new CopyOnWriteArrayList<>(playerInfoCollection);
                List<String> blacklistedUsers = getBlacklistedUsers();

                ExecutorService executor = Executors.newFixedThreadPool(8); // 8 to slightly help with ratelimit
                List<Future<Boolean>> futures = new CopyOnWriteArrayList<>();

                for (NetworkPlayerInfo playerInfo : safePlayerInfoCollection) {
                    futures.add(executor.submit(() -> {
                        String uuid = playerInfo.getGameProfile().getId().toString();
                        String ign = playerInfo.getGameProfile().getName();
                        String tabDisplayName = gTBN(ign);

                        if ("NONE".equals(tabDisplayName)) return false;

                        if (blacklistedUsers.contains(ign)) {
                            playSound("random.levelup", 1.0f, 1.0f);
                            mc.thePlayer.addChatMessage(new ChatComponentText(
                                    prefix + EnumChatFormatting.RED + tabDisplayName + EnumChatFormatting.RED + " is BLACKLISTED!"));
                            return true;
                        }

                        PlayerData playerData = getPlayerData(uuid);
                        if (playerData != null) {
                            double fkdr = playerData.getFkdr();
                            if (fkdr >= getMinFKDR()) {
                                playSound("note.pling", 1.0f, 1.0f);
                                mc.thePlayer.addChatMessage(new ChatComponentText(
                                        prefix + EnumChatFormatting.DARK_PURPLE + tabDisplayName + EnumChatFormatting.AQUA + " has FKDR: " + EnumChatFormatting.DARK_PURPLE + fkdr));
                                return true;
                            }
                        } else {
                            System.out.println(ign + " is NOT a valid Hypixel player.");
                            playSound("random.levelup", 1.0f, 1.0f);
                            mc.thePlayer.addChatMessage(new ChatComponentText(
                                    prefix + EnumChatFormatting.LIGHT_PURPLE + tabDisplayName + EnumChatFormatting.LIGHT_PURPLE + " is nicked!"));
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

                mc.thePlayer.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.GREEN + "Checks completed."));
                if (!anyValidPlayer && autoRqEnabled && !autoRqCommand.isEmpty()) {
                    mc.thePlayer.sendChatMessage(autoRqCommand);
                    mc.thePlayer.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.GREEN + "Auto-RQ executed: " + autoRqCommand));
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
            mc.thePlayer.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "Invalid FKDR value! Please enter a valid number."));
        }
    }

    public void setAutoRqEnabled(boolean enabled) {
        autoRqEnabled = enabled;
    }

    public void setAutoRqCommand(String command) {
        autoRqCommand = command;
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
                mc.thePlayer.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "Error, Key Not Set!"));
                return null;
            }
            URL url = new URL("https://api.hypixel.net/player?key=" + currentApiKey + "&uuid=" + uuid);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                response.append((char)c);
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
                fkdr = (double)finalKills / finalDeaths;
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
        ScorePlayerTeam playerTeam = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(playerName);
        if (playerTeam == null) {
            return "NONE";
        }
        int length = playerTeam.getColorPrefix().length();
        if (length == 10) {
            return playerTeam.getColorPrefix() + playerName + playerTeam.getColorSuffix();
        }
        return "NONE";
    }

    public boolean isNoStats(PlayerData playerData) {
        return playerData == null || playerData.getFkdr() == 0.0;
    }

    private void playSound(String soundName, float volume, float pitch) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().theWorld.playSound(
                    Minecraft.getMinecraft().thePlayer.posX,
                    Minecraft.getMinecraft().thePlayer.posY,
                    Minecraft.getMinecraft().thePlayer.posZ,
                    soundName,
                    volume,
                    pitch,
                    false
            );
        }
    }

    public void tPC() {
        playerCheckEnabled = !playerCheckEnabled;
    }

    public boolean isPCenabled() {
        return playerCheckEnabled;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!hudVisible) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRendererObj;

        int x = 10;
        int y = 10;
        int lineSpacing = 10;
        int color = 0xFFFFFF;

        String autoRqStatus = "auto-rq: " + (isAutoRqEnabled() ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled");
        fontRenderer.drawStringWithShadow(autoRqStatus, x, y, color);

        if (isAutoRqEnabled()) {
            y += lineSpacing;
            String autoRqCommandText = "cmd: " + EnumChatFormatting.AQUA + getAutoRqCommand();
            fontRenderer.drawStringWithShadow(autoRqCommandText, x, y, color);
        }

        y += lineSpacing;
        String snipeCommand = "stats-check: " + (isPCenabled() ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled");
        fontRenderer.drawStringWithShadow(snipeCommand, x, y, color);
    }

    private boolean hudVisible = true;

    public void tHudVisibility() {
        hudVisible = !hudVisible;
    }

    public boolean isHudVisible() {
        return hudVisible;
    }
}
