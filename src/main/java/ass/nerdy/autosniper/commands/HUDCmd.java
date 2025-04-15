package ass.nerdy.autosniper.commands;

import ass.nerdy.autosniper.AutoSniper;
import ass.nerdy.autosniper.Checker;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;


public class HUDCmd {
    private final Checker checker;

    public HUDCmd(Checker checker) {
        this.checker = checker;

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                register(dispatcher)
        );
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("hud")
                .executes(context -> {
                    checker.toggleOverlay();
                    boolean currentStatus = checker.isOverlayVisible();
                    updateHudConfig(currentStatus);
                    AutoSniper.log(Formatting.GREEN + "HUD is now "
                            + (currentStatus
                            ? Formatting.AQUA + "visible"
                            : Formatting.RED + "hidden")
                    );
                    return 1;
                })
        );
    }

    private void updateHudConfig(boolean visible) {
        AutoSniper.config.hudVisible = visible;
        AutoSniper.config.save();
    }
}