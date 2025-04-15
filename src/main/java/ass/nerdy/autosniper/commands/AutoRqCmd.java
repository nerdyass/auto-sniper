package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import ass.nerdy.autosniper.Checker;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;

public class AutoRqCmd {

    public AutoRqCmd() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("autorg")
                        .executes(ctx -> {
                            AutoSniper.config.autoRqEnabled = !AutoSniper.config.autoRqEnabled;
                            AutoSniper.config.save();

                            AutoSniper.log(Formatting.GREEN + "Auto-RQ is now " + (
                                    AutoSniper.config.autoRqEnabled
                                            ? Formatting.AQUA + "enabled"
                                            : Formatting.RED + "disabled"
                            ));

                            return 1;
                        })
        );
    }
}