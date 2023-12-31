package net.flarepowered.core.TML.components.player;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.text.Message;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

import static org.bukkit.Bukkit.getServer;

public class MessageComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[MESSAGE(\\((.+)\\))?] (.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(3) == null)
                throw new ComponentException("The component [MESSAGE] has no message. We are skipping this item.");
            if(matcher.group(2) != null) {
                if(matcher.group(2).contains("nopapi")) {
                    Message.sendMessage(matcher.group(3), player);
                } else if(matcher.group(2).contains("broadcast")) {
                    getServer().spigot().broadcast(Message.formatComponents(matcher.group(3), player));
                }
            } else
                Message.sendMessage(matcher.group(3), player);
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
