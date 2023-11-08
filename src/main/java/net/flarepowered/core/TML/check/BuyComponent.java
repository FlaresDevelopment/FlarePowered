package net.flarepowered.core.TML.check;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.text.Message;
import net.flarepowered.core.text.other.StringUtils;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.other.exceptions.ComponentException;
import net.flarepowered.utils.DependencyManager;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuyComponent implements Component {

    Pattern pattern = Pattern.compile("(?i)\\[buy\\((tokens|money|mobcoins)\\)] (\\d+\\.?\\d*)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = this.pattern.matcher(string);
        if (matcher.find()) {
            if (matcher.group(1) == null)
                throw new ComponentException("The component [buy(" + matcher.group(1) + ")] has no message. We are skipping this item.");
            if (matcher.group(2) == null)
                throw new ComponentException("The component [buy(" + matcher.group(1) + ")] has no message. We are skipping this item.");
            switch (matcher.group(1).toLowerCase(Locale.ROOT)) {
                case "money":
                    if(!DependencyManager.GET.isPluginLoaded(DependencyManager.Dependency.Vault))
                        throw new ComponentException("This server has no economy, so we cant use " + matcher.group());
                    if(DependencyManager.GET.getVaultEconomy().getBalance(player) >= Double.parseDouble(matcher.group(2))) {
                        DependencyManager.GET.getVaultEconomy().withdrawPlayer(player, Double.parseDouble(matcher.group(2)));
                        return TMLState.COMPLETED;
                    }
                    Message.sendLocalizedMessage("buy_failed", player, new Replace("%pl_amount%", matcher.group(2)));
                    break;
                case "tokens":
                    if(!DependencyManager.GET.isPluginLoaded(DependencyManager.Dependency.FlareTokens))
                        throw new ComponentException("This server has no economy, so we cant use " + matcher.group());
                    try {
//                        if(TokensPlayer.warpPlayer(player.getUniqueId()).getTokens() >= Integer.parseInt(matcher.group(2))) {
//                            TokensPlayer.warpPlayer(player.getUniqueId()).removeTokens(Integer.parseInt(matcher.group(2)));
//                            return TMLState.COMPLETED;
//                        }
                        Message.sendLocalizedMessage("buy_failed", player, new Replace("%pl_amount%", matcher.group(2)));
                    } catch (NumberFormatException err) {
                        throw new ComponentException("FlareTokens accepts only natural numbers, for example 1,4,2,6,7 etc");
                    }
                    break;
                    case "mobcoins":
                        // TODO add the mobcoins buy support
            }
            return TMLState.FORCED_QUIT;
        } else {
            return TMLState.NOT_A_MATCH;
        }
    }
}
