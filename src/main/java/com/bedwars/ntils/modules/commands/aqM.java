package com.bedwars.ntils.modules.commands;

import com.bedwars.ntils.modules.pC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class aqM {
    private final pC checker;
    private final Map<String, String> modeMap = new HashMap<>();
    private static final File CONFIG_FILE = new File("config/autosniper.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    final String prefix = Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + "N" + Formatting.GRAY + "] ";

    public aqM(pC checker) {
        this.checker = checker;
        modeMap.put("1s", "/play bedwars_eight_one");
        modeMap.put("2s", "/play bedwars_eight_two");
        modeMap.put("3s", "/play bedwars_four_three");
        modeMap.put("4s", "/play bedwars_four_four");
        modeMap.put("4v4", "/play bedwars_two_four");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("mode")
                .then(ClientCommandManager.argument("mode", StringArgumentType.word())
                        .executes(ctx -> {
                            String input = StringArgumentType.getString(ctx, "mode").toLowerCase();
                            String command = modeMap.getOrDefault(input, String.join(" ", input));

                            checker.setAutoRqCommand(command);
                            JsonAutoRq(command);

                            ctx.getSource().sendFeedback(Text.literal(prefix + Formatting.GREEN + "Auto-RQ mode set to: " + Formatting.AQUA + command));
                            return 1;
                        }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.literal(prefix + Formatting.RED + "You must provide a mode to set!"));
                    return 0;
                })
        );
    }

    private void JsonAutoRq(String command) {
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

            config.put("autoRqCommand", command);

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
