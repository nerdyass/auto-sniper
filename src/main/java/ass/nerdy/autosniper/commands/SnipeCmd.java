package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import ass.nerdy.autosniper.Checker;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;

public class SnipeCmd {
    private final Checker checker;

    public SnipeCmd(Checker checker) {
        this.checker = checker;
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("snipe")
                .executes(context -> {
                    checker.togglePlayerCheckEnabled();
                    AutoSniper.config.save();
                    AutoSniper.log(Formatting.GREEN + "Player stats checking is now "
                            + (AutoSniper.config.playerCheckEnabled
                            ? Formatting.AQUA + "enabled"
                            : Formatting.RED + "disabled"));

                    return 1;
                })
        );
    }
}