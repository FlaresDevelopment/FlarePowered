package net.flarepowered.core.TML.components.economy;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.ComponentException;
import net.flarepowered.utils.DependencyManager;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokensComponent implements Component {

    Pattern pattern = Pattern.compile("(?i)\\[tokens_(give|set|remove)] (.+)");

    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = this.pattern.matcher(string);
        if (matcher.find()) {
            if(!DependencyManager.GET.isPluginLoaded(DependencyManager.Dependency.FlareTokens)) {
                throw new ComponentException("FlareTokens is not enabled on this server, so we cant use " + matcher.group());
            }
            if (matcher.group(2) == null)
                throw new ComponentException("The component [tokens_" + matcher.group(1) + "] has no message. We are skipping this item.");
            switch (matcher.group(1).toLowerCase(Locale.ROOT)) {
//                case "give":
//                    ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId()).giveTokens(Integer.parseInt(matcher.group(2)));
//                    break;
//                case "set":
//                    ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId()).setTokens(Integer.parseInt(matcher.group(2)));
//                    break;
//                case "remove":
//                    ServiceHandler.SERVICE.getDataService().warpPlayer(player.getUniqueId()).removeTokens(Integer.parseInt(matcher.group(2)));
//                    break;
            }
            return TMLState.COMPLETED;
        } else {
            return TMLState.NOT_A_MATCH;
        }
    }

}
