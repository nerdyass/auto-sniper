package com.bedwars.ntils.modules.commands;

import com.bedwars.ntils.modules.pC;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class tPC extends CommandBase {
    final String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "N" + EnumChatFormatting.GRAY + "] ";

    private final pC checker;

    public tPC(pC checker) {
        this.checker = checker;
    }

    @Override
    public String getCommandName() {
        return "snipe";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/snipe - Toggle player checking on or off.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        checker.tPC();
        boolean currentStatus = checker.isPCenabled();
        updatePCC(currentStatus);
        sender.addChatMessage(
                new ChatComponentText(
                        prefix + EnumChatFormatting.GREEN + "Player stats checking is now " + (currentStatus ? EnumChatFormatting.AQUA + "enabled" : EnumChatFormatting.RED + "disabled") + "."
                )
        );
    }

    private void updatePCC(boolean enabled) {
        File configFile = new File("config/autosniper.json");

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reader);
                JsonObject existingConfig = element.getAsJsonObject();

                existingConfig.addProperty("playerCheckEnabled", enabled);

                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(existingConfig.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
