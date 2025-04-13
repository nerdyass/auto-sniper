package com.bedwars.ntils;

import com.bedwars.ntils.modules.*;
import com.bedwars.ntils.modules.commands.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ntils.MODID, version = ntils.VERSION)
public class ntils {
    public static final String MODID = "AS";
    public static final String VERSION = "2.6";

    private pC checker;

    @EventHandler
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
