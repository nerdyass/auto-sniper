package com.bedwars.ntils;

import com.bedwars.ntils.modules.*;
import com.bedwars.ntils.modules.commands.*;
import com.bedwars.ntils.orbit.EventBus;
import com.bedwars.ntils.orbit.EventHandler;
import com.bedwars.ntils.orbit.IEventBus;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.lang.invoke.MethodHandles;

public class ntils implements ModInitializer {
    public static final String MODID = "AS";
    public static final String VERSION = "2.6";
    public static IEventBus EVENTBUS = new EventBus();
    public static String packagePrefix = "com.bedwars.ntils";

    private pC checker;

    @EventHandler
    public void onInitialize() {
        System.out.println("Initializing " + MODID + " v" + VERSION);
        cF.InitConfig();
        EVENTBUS.registerLambdaFactory(packagePrefix, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        // not needed because we're not initializing using fucking mixins nigger
        // EVENTBUS.subscribe(this);
        checker = new pC();
        EVENTBUS.subscribe(checker);
        // MinecraftForge.EVENT_BUS.register(checker);

        new tARQ(checker);
        new tPC(checker);
        new aqM(checker);
        new tH(checker);
        new fS(checker);

        new aK();
        new bL();
    }
}
