package net.flarepowered.core.TML.components.player;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.text.StringUtils;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class PlayerComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[PLAYER] (.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [PLAYER] has no console command. We are skipping this item.");
            player.performCommand(StringUtils.formatMessage(matcher.group(1), player));
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
