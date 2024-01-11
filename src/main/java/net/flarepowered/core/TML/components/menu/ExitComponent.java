package net.flarepowered.core.TML.components.menu;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.neo.ui.MenuArchitect;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExitComponent implements Component {

    /**
     * [remove_item] [slot] (amount)
     * [remove_item] [slot]
     */
    Pattern pattern = Pattern.compile("(?i)\\[(exit|close|bye|shutdown|leave|go|down|kill|terminate|stop|euthanasiate)]");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher main = pattern.matcher(string);
        if(main.find()) {
            if(!MenuArchitect.MENU.getHolder().containsKey(player)) throw new ComponentException("The " + main.group() + " component MUST be inside in a menu!");
            player.closeInventory();
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }

}
