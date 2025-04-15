package com.bedwars.ntils.event.events;

import net.minecraft.client.gui.DrawContext;

@SuppressWarnings("all")
public class HudRenderEvent {

    private static final HudRenderEvent INSTANCE = new HudRenderEvent();

    public DrawContext drawContext;
    public float tickDelta;

    public static HudRenderEvent get(DrawContext drawContext, float tickDelta) {
        INSTANCE.drawContext = drawContext;
        INSTANCE.tickDelta = tickDelta;
        return INSTANCE;
    }

}