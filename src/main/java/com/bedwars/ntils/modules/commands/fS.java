package com.bedwars.ntils.modules.commands;

import com.bedwars.ntils.modules.pC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.HashMap;

public class fS extends CommandBase {
    private final pC checker;
    private static final File CONFIG_FILE = new File("config/autosniper.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "N" + EnumChatFormatting.GRAY + "] ";

    public fS(pC checker) {
        this.checker = checker;
    }

    @Override
    public String getCommandName() {
        return "minfkdr";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/minfkdr [fkdr value]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "You must provide an FKDR value to set!"));
            return;
        }

        try {
            double fkdrValue = Double.parseDouble(args[0]);

            JsonFKDR(fkdrValue);

            checker.setFkdrValue(String.valueOf(fkdrValue));

            sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.GREEN + "Minimum FKDR set to: " + EnumChatFormatting.AQUA + fkdrValue));
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "Invalid number. Please enter a valid FKDR."));
        }
    }

    private void JsonFKDR(double fkdrValue) {
        try {
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }

            HashMap<String, Object> config = new HashMap<>();
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    config = GSON.fromJson(reader, HashMap.class);
                }
            }

            config.put("fkdrValue", fkdrValue);

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
