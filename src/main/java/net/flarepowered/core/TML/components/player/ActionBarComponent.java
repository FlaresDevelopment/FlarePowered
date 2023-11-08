package net.flarepowered.core.TML.components.player;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.text.Message;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class ActionBarComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[actionbar] (.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [ACTIONBAR] has no message. We are skipping this item.");
            Message.sendActionBar(player, matcher.group(1));
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
