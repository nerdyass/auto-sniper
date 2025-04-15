package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;

public class MinFKDRCmd {

    public MinFKDRCmd() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("minfkdr")
                .then(ClientCommandManager.argument("fkdr", DoubleArgumentType.doubleArg())
                        .executes(ctx -> {
                            double fkdrValue = DoubleArgumentType.getDouble(ctx, "fkdr");
                            if (AutoSniper.config.setFKDRValue(String.valueOf(fkdrValue))) {
                                AutoSniper.log(Formatting.GREEN + "Minimum FKDR set to: " + Formatting.AQUA + fkdrValue);
                            } else {
                                AutoSniper.log(Formatting.RED + "Invalid number");
                            }
                            return 1;
                        }))
                .executes(ctx -> {
                    AutoSniper.log(Formatting.RED + "You must provide an FKDR value to set!");
                    return 0;
                })
        );
    }
}