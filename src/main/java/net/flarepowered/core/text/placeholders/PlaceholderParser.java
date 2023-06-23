package net.flarepowered.core.text.placeholders;

import net.flarepowered.FlarePowered;
import org.bukkit.entity.Player;

public class PlaceholderParser {

    public static String parse(String message, Object player) {
        for (Placeholder placeholder : FlarePowered.LIB.getPlaceholders()) {
            if (placeholder != null)
                message = placeholder.process(message, player);
        }
        return message;
    }
}
