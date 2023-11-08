package net.flarepowered.core.TML.components.menu;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

import static org.bukkit.entity.ItemDisplay.ItemDisplayTransform.GUI;

public class CloseMenuComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[(EXIT|CLOSE)]");

    //(\w+=(\S+))
    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            player.closeInventory();
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
