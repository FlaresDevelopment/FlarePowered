package net.flarepowered.core.TML.check;

import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.text.Message;
import net.flarepowered.core.text.other.StringUtils;
import net.flarepowered.other.exceptions.CheckException;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionCheck implements Requirement {

    List<Pattern> patternList = Arrays.asList(
            Pattern.compile("\\[?(require|check)\\(expression;(.*?);(.*?);(.*?)\\)]?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\[?(require|check)\\(expression\\)]?\\s*(\\S*) (\\S*) (\\S*)", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public TMLState run(String string, Player player) throws CheckException {
        if(!string.toLowerCase(Locale.ROOT).contains("expression"))
            return TMLState.NOT_A_MATCH;
        for(Pattern pattern : patternList) {
            Matcher matcher = pattern.matcher(string);
            if(matcher.find()) {
                if(matcher.group(2) == null)
                    throw new CheckException("The component [CHECK(expression)] has no part1. We are skipping this item.");
                if(matcher.group(3) == null)
                    throw new CheckException("The component [CHECK(expression)] has no operator (==;!=;<;>;<=;>=). We are skipping this item.");
                if(matcher.group(4) == null)
                    throw new CheckException("The component [CHECK(expression)] has no part2. We are skipping this item.");
                if(check(Message.format(matcher.group(2), player), matcher.group(3), Message.format(matcher.group(4), player)))
                    return TMLState.CHECK_SUCCESS; else return TMLState.CHECK_FALL;
            }
        }
        return TMLState.NOT_A_MATCH;
    }
    private boolean check(String part1, String operator1, String part2) throws CheckException {
        switch (operator1) {
            case "==":
                return part1.equals(part2);
            case "!=":
                return !part1.equals(part2);
            case ">=":
                BigDecimal bd1 = new BigDecimal(part1);
                BigDecimal bd2 = new BigDecimal(part2);
                return (bd1.compareTo(bd2) >= 0);
            case "<=":
                BigDecimal bd3 = new BigDecimal(part1);
                BigDecimal bd4 = new BigDecimal(part2);
                return (bd3.compareTo(bd4) <= 0);
            case ">":
                BigDecimal bd5 = new BigDecimal(part1);
                BigDecimal bd6 = new BigDecimal(part2);
                return (bd5.compareTo(bd6) > 0);
            case "<":
                BigDecimal bd7 = new BigDecimal(part1);
                BigDecimal bd8 = new BigDecimal(part2);
                return (bd7.compareTo(bd8) < 0);
        }
        throw new CheckException("A problem trying to evaluate the expression, the operator might be wrong, please make sure that you are using those operators: (==;!=;<;>;<=;>=)");
    }
}
