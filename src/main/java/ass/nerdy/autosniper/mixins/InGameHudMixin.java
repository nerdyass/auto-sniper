package ass.nerdy.autosniper.mixins;

import ass.nerdy.autosniper.event.events.HudRenderEvent;
import ass.nerdy.autosniper.AutoSniper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        AutoSniper.EVENTBUS.post(HudRenderEvent.get(context, tickDelta));
    }
}
