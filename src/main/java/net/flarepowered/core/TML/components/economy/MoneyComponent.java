package net.flarepowered.core.TML.components.economy;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.ComponentException;
import net.flarepowered.utils.DependencyManager;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyComponent implements Component {

    Pattern pattern = Pattern.compile("(?i)\\[money_(give|set|remove)] (.+)");

    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = this.pattern.matcher(string);
        if (matcher.find()) {
            if(!DependencyManager.GET.isPluginLoaded(DependencyManager.Dependency.Vault)) {
                throw new ComponentException("This server has no economy, so we cant use " + matcher.group());
            }
            if (matcher.group(2) == null)
                throw new ComponentException("The component [money_" + matcher.group(1) + "] has no message. We are skipping this item.");
            switch (matcher.group(1).toLowerCase(Locale.ROOT)) {
                case "give":
                    DependencyManager.GET.getVaultEconomy().depositPlayer(player, Double.parseDouble(matcher.group(2)));
                    break;
                case "set":
                    DependencyManager.GET.getVaultEconomy().withdrawPlayer(player, DependencyManager.GET.getVaultEconomy().getBalance(player));
                    DependencyManager.GET.getVaultEconomy().depositPlayer(player, Double.parseDouble(matcher.group(2)));
                    break;
                case "remove":
                    DependencyManager.GET.getVaultEconomy().withdrawPlayer(player, Double.parseDouble(matcher.group(2)));
                    break;
            }
            return TMLState.COMPLETED;
        } else {
            return TMLState.NOT_A_MATCH;
        }
    }

}
