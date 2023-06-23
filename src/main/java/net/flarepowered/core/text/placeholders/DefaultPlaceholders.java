package net.flarepowered.core.text.placeholders;

import org.bukkit.entity.Player;

public class DefaultPlaceholders implements Placeholder {
    @Override
    public String process(String message, Object player) {
        message = message.replace("%pl_player%", player instanceof Player ? ((Player) player).getName() : "Console")
                .replace("%pl_world%", player instanceof Player ? ((Player) player).getName() : "RAM and CPU");
        return message;
    }
}
