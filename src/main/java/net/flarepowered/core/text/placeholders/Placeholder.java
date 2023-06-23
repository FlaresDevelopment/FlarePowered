package net.flarepowered.core.text.placeholders;

import org.bukkit.entity.Player;

public interface Placeholder {

    String process(String message, Object player);

}
