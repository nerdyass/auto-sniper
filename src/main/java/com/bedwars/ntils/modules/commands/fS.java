package com.bedwars.ntils.modules.commands;

import com.bedwars.ntils.modules.pC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class fS {
    private final pC checker;
    private static final File CONFIG_FILE = new File("config/autosniper.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String prefix = Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + "N" + Formatting.GRAY + "] ";

    public fS(pC checker) {
        this.checker = checker;

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("minfkdr")
                .then(ClientCommandManager.argument("fkdr", DoubleArgumentType.doubleArg())
                        .executes(ctx -> {
                            double fkdrValue = DoubleArgumentType.getDouble(ctx, "fkdr");
                            checker.setFkdrValue(String.valueOf(fkdrValue));
                            saveFkdrValue(fkdrValue);

                            ctx.getSource().sendFeedback(
                                    Text.literal(prefix + Formatting.GREEN + "Minimum FKDR set to: " + Formatting.AQUA + fkdrValue)
                            );
                            return 1;
                        }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(
                            Text.literal(prefix + Formatting.RED + "You must provide an FKDR value to set!")
                    );
                    return 0;
                })
        );
    }

    private void saveFkdrValue(double fkdrValue) {
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
}
