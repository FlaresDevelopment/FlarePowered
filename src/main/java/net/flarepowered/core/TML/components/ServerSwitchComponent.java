package net.flarepowered.core.TML.components;

import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
public class ServerSwitchComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[switch_server] (.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [server_switch] has no console command. We are skipping this item.");
            // TODO Write bungee stuff
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
