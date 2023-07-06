package net.flarepowered.core.TML.components.menu;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.menus.MenuManager;
import net.flarepowered.core.text.StringUtils;
import net.flarepowered.other.exceptions.ComponentException;
import net.flarepowered.other.exceptions.MenuRenderException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class OpenMenuComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[open_menu] (.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        if(FlarePowered.LIB.getMenuManager() == null)
            return TMLState.NOT_A_MATCH;
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [open_menu] has no menu name command. We are skipping this item.");
            if(FlarePowered.LIB.getMenuManager().CACHED_MENUS.containsKey(matcher.group(1))) {
                try {
                    FlarePowered.LIB.getMenuManager().renderMenuToPlayer(player, matcher.group(1));
                } catch (MenuRenderException e) {
                    throw new RuntimeException(e);
                }
            }
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
