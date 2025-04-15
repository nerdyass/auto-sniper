package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class ModeCmd {
    private final Map<String, String> modeMap = new HashMap<>();

    public ModeCmd() {
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

                            AutoSniper.config.autoRqCommand = command;
                            AutoSniper.config.save();

                            AutoSniper.log(Formatting.GREEN + "Auto-RQ mode set to: " + Formatting.AQUA + command);
                            return 1;
                        }))
                .executes(ctx -> {
                    AutoSniper.log(Formatting.RED + "You must provide a mode to set!");
                    return 0;
                })
        );
    }
}