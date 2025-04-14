package ass.nerdy.autosniper;

import ass.nerdy.autosniper.commands.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = AutoSniper.MODID, version = AutoSniper.VERSION)
public class AutoSniper {
    public static final String MODID = "AS"; // TODO: detectable by hypixel but prob doesn't matter
    public static final String VERSION = "2.6";

    public static Minecraft mc;
    public static Config config;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        mc = Minecraft.getMinecraft();

        config = new Config();
        config.load();

        MinecraftForge.EVENT_BUS.register(this);

        Checker checker = new Checker();
        ClientCommandHandler.instance.registerCommand(new SnipeCmd(checker));
        ClientCommandHandler.instance.registerCommand(new AutoRqCmd());
        ClientCommandHandler.instance.registerCommand(new ModeCmd());
        ClientCommandHandler.instance.registerCommand(new HUDCmd(checker));
        ClientCommandHandler.instance.registerCommand(new MinFKDRCmd());
        ClientCommandHandler.instance.registerCommand(new KeyCmd());
        ClientCommandHandler.instance.registerCommand(new TargetCmd());
    }

    public static void log(String message) {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(
                "§7[§dN§7] §r" + message
        ));
    }
}
