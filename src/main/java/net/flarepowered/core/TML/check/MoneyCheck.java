package net.flarepowered.core.TML.check;

import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.CheckException;
import net.flarepowered.utils.DependencyManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyCheck implements Requirement {

    List<Pattern> patternList = Arrays.asList(
            Pattern.compile("\\[?(require|check)\\(money;(.*?)\\)]?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(dev_tag_no_use=args)?\\[?(require|check)\\(money\\)]? ((\\w+)=(\\S+))\\s*((\\w+)=(\\S+))?", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public TMLState run(String string, Player player) throws CheckException {
        for(Pattern pattern : patternList) {
            Matcher matcher = pattern.matcher(string);
            String amount = "0.0";
            boolean remove = false;
            if (matcher.find()) {
                if(pattern.toString().contains("dev_tag_no_use=args")) {
                    if(matcher.group(4).equals("amount"))
                        amount = matcher.group(5);
                    if(matcher.group(7) != null)
                        if(matcher.group(7).equals("remove"))
                            remove = Boolean.parseBoolean(matcher.group(8));
                } else {
                    amount = matcher.group(2);
                }
                if (DependencyManager.GET.getVaultEconomy().getBalance(player) >= Double.parseDouble(amount)) {
                    if(remove)
                        DependencyManager.GET.getVaultEconomy().withdrawPlayer(player, Double.parseDouble(amount));
                    return TMLState.CHECK_SUCCESS;
                }
                else return TMLState.CHECK_FALL;
            }
        }
        return TMLState.NOT_A_MATCH;
    }
}
