package net.flarepowered.core.TML.check;

import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.CheckException;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionCheck implements Requirement {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[?(require|check)\\(permission,(.*?)\\)]?", Pattern.CASE_INSENSITIVE);

    @Override
    public TMLState run(String string, Player player) throws CheckException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(2) == null)
                throw new CheckException("The component [CHECK(permission,)] has no string. We are skipping this item.");
            if(player.hasPermission(matcher.group(2)))
                return TMLState.CHECK_SUCCESS; else return TMLState.CHECK_FALL;
        }
        return TMLState.NOT_A_MATCH;
    }
}
