package net.flarepowered.core.TML.components;

import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.text.Message;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class ConsoleComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[CONSOLE] (.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [CONSOLE] has no command. We are skipping this item.");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Message.format(matcher.group(1), player));
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
