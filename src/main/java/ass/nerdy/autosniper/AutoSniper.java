package ass.nerdy.autosniper;

import ass.nerdy.autosniper.modules.cF;
import ass.nerdy.autosniper.modules.commands.*;
import ass.nerdy.autosniper.modules.pC;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = AutoSniper.MODID, version = AutoSniper.VERSION)
public class AutoSniper {
    public static final String MODID = "AS";
    public static final String VERSION = "2.6";

    private pC checker;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        cF.InitConfig();
        MinecraftForge.EVENT_BUS.register(this);
        checker = new pC();
        MinecraftForge.EVENT_BUS.register(checker);

        ClientCommandHandler.instance.registerCommand(new tPC(checker));
        ClientCommandHandler.instance.registerCommand(new tARQ(checker));
        ClientCommandHandler.instance.registerCommand(new aqM(checker));
        ClientCommandHandler.instance.registerCommand(new tH(checker));
        ClientCommandHandler.instance.registerCommand(new fS(checker));
        ClientCommandHandler.instance.registerCommand(new aK());
        ClientCommandHandler.instance.registerCommand(new bL());
    }
}
