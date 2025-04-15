package ass.nerdy.autosniper;

import ass.nerdy.autosniper.commands.*;
import ass.nerdy.autosniper.orbit.EventBus;
import ass.nerdy.autosniper.orbit.IEventBus;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static net.fabricmc.loader.impl.FabricLoaderImpl.MOD_ID;

public class AutoSniper implements ModInitializer {
    public static final String MODID = "AS"; // NOT DETECTABLE BY HYPIXEL ON 1.20 I THINK AUGHHHH
    public static final String VERSION = "2.6";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static IEventBus EVENTBUS = new EventBus();
    public static String packagePrefix = "ass.nerdy.autosniper";

    public static MinecraftClient mc;
    public static Config config;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing " + MODID + " version " + VERSION);
        mc = MinecraftClient.getInstance();

        config = new Config();
        config.load();
        EVENTBUS.registerLambdaFactory(packagePrefix, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

        Checker checker = new Checker();
        EVENTBUS.subscribe(checker);

        // ouuhhhhhh
        new SnipeCmd(checker);
        new AutoRqCmd();
        new ModeCmd();
        new HUDCmd(checker);
        new MinFKDRCmd();
        new KeyCmd();
        new TargetCmd();
    }

    public static void log(String message) { // wow so much simpler
        mc.player.sendMessage(Text.literal(
                "§7[§dN§7] §r" + message
        ));
    }
}