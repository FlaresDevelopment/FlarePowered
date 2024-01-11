package net.flarepowered.core.TML.components.menu;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.neo.ui.MenuArchitect;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class RemoveItemComponent implements Component {

    /**
     * [remove_item] [slot] (amount)
     * [remove_item] [slot]
     */
    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[remove_item]\\s+(\\d+)\\s*(\\d+)?");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null) throw new ComponentException("There is no slot to remove an item!");
            if(!MenuArchitect.MENU.getHolder().containsKey(player)) throw new ComponentException("The [remove_item] component MUST be inside in a menu!");
            if(matcher.group(2) == null)
                MenuArchitect.MENU.getHolder().get(player).removeItem(Integer.parseInt(matcher.group(1)));
            else
                MenuArchitect.MENU.getHolder().get(player).removeItem(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }

}
