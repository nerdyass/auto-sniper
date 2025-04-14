package ass.nerdy.autosniper.modules;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class cU {
    //didnt use this cus lazy, but it's here if you'd like to make it easier -> you could also add the prefix here.
    public static void cm(ICommandSender sender, String message) {
        sender.addChatMessage(new ChatComponentText(message));
    }
}
