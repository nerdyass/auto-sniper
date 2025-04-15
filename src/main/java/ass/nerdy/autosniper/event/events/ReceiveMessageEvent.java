package ass.nerdy.autosniper.event.events;

import ass.nerdy.autosniper.event.Cancellable;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;

// ehm ehm, stolen from meteor because i'm a lazy fuck
public class ReceiveMessageEvent extends Cancellable {
    private static final ReceiveMessageEvent INSTANCE = new ReceiveMessageEvent();

    private Text message;
    private MessageIndicator indicator;
    private boolean modified;
    public int id;

    public static ReceiveMessageEvent get(Text message, MessageIndicator indicator) {
        INSTANCE.setCancelled(false);
        INSTANCE.message = message;
        INSTANCE.indicator = indicator;
        INSTANCE.modified = false;
        return INSTANCE;
    }

    public Text getMessage() {
        return message;
    }

    public MessageIndicator getIndicator() {
        return indicator;
    }

    public void setMessage(Text message) {
        this.message = message;
        this.modified = true;
    }

    public void setIndicator(MessageIndicator indicator) {
        this.indicator = indicator;
        this.modified = true;
    }

    public boolean isModified() {
        return modified;
    }
}