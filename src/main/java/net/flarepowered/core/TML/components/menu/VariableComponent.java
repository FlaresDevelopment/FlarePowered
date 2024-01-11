package net.flarepowered.core.TML.components.menu;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.neo.ui.MenuArchitect;
import net.flarepowered.neo.ui.contents.UI;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableComponent implements Component {

    /**
     * [remove_item] [slot] (amount)
     * [remove_item] [slot]
     */
    Pattern pattern = Pattern.compile("(?i)\\[(set_variable|add_to_variable|remove_from_variable)]\\s+(\\S+)\\s*(.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher main = pattern.matcher(string);
        if(main.find()) {
            if(main.group(1) == null) throw new ComponentException("You need to use: [(set_variable|add_to_variable|remove_from_variable)] (var) (value)");
            if(main.group(2) == null) throw new ComponentException("You must include a variable name!");
            if(main.group(3) == null) throw new ComponentException("You have to add value to set/remove/add to the variable!");
            if(!MenuArchitect.MENU.getHolder().containsKey(player)) throw new ComponentException("The " + main.group() + " component MUST be inside in a menu!");
            if(!MenuArchitect.MENU.getHolder().get(player).getLocalVariables().containsKey(main.group(2))) throw new ComponentException("There is not a variable by this name!");
            UI ui = MenuArchitect.MENU.getHolder().get(player);
            switch (main.group(1).toLowerCase(Locale.ROOT)) {
                case "add_to_variable":
                    if(!(ui.getLocalVariables().get(main.group(2)).getVariable() instanceof Integer))
                        throw new ComponentException("You cant add to a NON number variable!");
                    if(!main.group(3).matches("\\d+"))
                        throw new ComponentException("You cant add to a NON number to a number variable!");
                    ui.getLocalVariables().get(main.group(2)).setVariable(
                            (Integer) ui.getLocalVariables().get(main.group(2)).getVariable() + Integer.parseInt(main.group(3)));
                    ui.getReplacesList().get(main.group(2)).to = ui.getLocalVariables().get(main.group(2)).getVariable().toString();
                    break;
                case "remove_from_variable":
                    if(!(ui.getLocalVariables().get(main.group(2)).getVariable() instanceof Integer))
                        throw new ComponentException("You cant remove to a NON number variable!");
                    if(!main.group(3).matches("\\d+"))
                        throw new ComponentException("You cant remove to a NON number to a number variable!");
                    ui.getLocalVariables().get(main.group(2)).setVariable(
                            (Integer) ui.getLocalVariables().get(main.group(2)).getVariable() - Integer.parseInt(main.group(3)));
                    ui.getReplacesList().get(main.group(2)).to = ui.getLocalVariables().get(main.group(2)).getVariable().toString();
                    break;
                case "set_variable":
                    ui.getLocalVariables().get(main.group(2)).setVariable(main.group(3));
                    ui.getReplacesList().get(main.group(2)).to = ui.getLocalVariables().get(main.group(2)).getVariable().toString();
                    break;
            }
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }

}
