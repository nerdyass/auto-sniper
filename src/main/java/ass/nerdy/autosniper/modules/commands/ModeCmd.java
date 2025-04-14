package ass.nerdy.autosniper.modules.commands;

import ass.nerdy.autosniper.modules.pC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ModeCmd extends CommandBase {
    private final pC checker;
    private final Map<String, String> modeMap = new HashMap<>();
    private static final File CONFIG_FILE = new File("config/autosniper.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    final String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "N" + EnumChatFormatting.GRAY + "] ";

    public ModeCmd(pC checker) {
        this.checker = checker;
        modeMap.put("1s", "/play bedwars_eight_one");
        modeMap.put("2s", "/play bedwars_eight_two");
        modeMap.put("3s", "/play bedwars_four_three");
        modeMap.put("4s", "/play bedwars_four_four");
        modeMap.put("4v4", "/play bedwars_two_four");
    }

    @Override
    public String getCommandName() {
        return "mode";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mode [mode]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.RED + "You must provide a mode to set!"));
            return;
        }

        String input = args[0].toLowerCase();
        String command = modeMap.getOrDefault(input, String.join(" ", args));

        checker.setAutoRqCommand(command);

        JsonAutoRq(command);

        sender.addChatMessage(new ChatComponentText(prefix + EnumChatFormatting.GREEN + "Auto-RQ mode set to: " + EnumChatFormatting.AQUA + command));
    }

    private void JsonAutoRq(String command) {
        try {
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }

            HashMap<String, Object> config = new HashMap<>();
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    config = GSON.fromJson(reader, HashMap.class);
                }
            }

            config.put("autoRqCommand", command);

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
