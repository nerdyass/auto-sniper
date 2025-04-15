package ass.nerdy.autosniper.event;

import ass.nerdy.autosniper.orbit.ICancellable;

@SuppressWarnings("all")
public class Cancellable implements ICancellable {
    private boolean cancelled = false;

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}