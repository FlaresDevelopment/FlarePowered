package net.flarepowered.core.TML.components.player;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.text.Message;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[title] (.+)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [MESSAGE] has no message. We are skipping this item.");
            if(matcher.group(1).matches("(title=(.+)) (subtitle=(.+))")) {
                Matcher matcher1 = Pattern.compile("(title=(.+)) (subtitle=(.+))").matcher(matcher.group(1));
                if(!matcher1.find()) return TMLState.NOT_A_MATCH;
                Message.playTitle(player, matcher1.group(2), matcher1.group(4), 10, 40, 10);
                return TMLState.COMPLETED;
            } else if(matcher.group(1).matches("(subtitle=(.+))")) {
                Matcher matcher1 = Pattern.compile("(subtitle=(.+))").matcher(matcher.group(1));
                if(!matcher1.find()) return TMLState.NOT_A_MATCH;
                Message.playTitle(player, "", matcher1.group(2), 10, 40, 10);
                return TMLState.COMPLETED;
            } if(matcher.group(1).matches("(title=(.+))")) {
                Matcher matcher1 = Pattern.compile("(title=(.+))").matcher(matcher.group(1));
                if(!matcher1.find()) return TMLState.NOT_A_MATCH;
                Message.playTitle(player, matcher1.group(2), "", 10, 40, 10);
                return TMLState.COMPLETED;
            }
        }
        return TMLState.NOT_A_MATCH;
    }
}
