package net.flarepowered.core.TML.components.player;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.core.text.bossbar.schematics.BossbarSchematic;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BossbarComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[bossbar] (.+)");

    @Override
    public TMLState run(String s, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()) {
            if(FlarePowered.LIB.getBossBarUtils().bossbars.get(player.getUniqueId()) != null) {
                String id = getContent(s, "id=\"([^\"]*)\"", "No_Key_By_This_Name");
                if (FlarePowered.LIB.getBossBarUtils().bossbars.get(player.getUniqueId()).containsKey(id)) {
                    FlarePowered.LIB.getBossBarUtils().bossbars.get(player.getUniqueId()).get(id).timePassed = 0;
                    return TMLState.COMPLETED;
                }
            }
            if(matcher.group(1) == null)
                throw new ComponentException("The component [CONSOLE] has no command. We are skipping this item.");
            String original = matcher.group(1);
            BossbarSchematic bossbarSchematic = new BossbarSchematic();
            bossbarSchematic.title = getContent(original, "message=\"([^\"]*)\"", "No Content");
            bossbarSchematic.screenTime = Integer.parseInt(getContent(original, "screen_time=\"([^\"]*)\"", "-1"));
            bossbarSchematic.barColor = BarColor.valueOf(getContent(original, "bar_color=\"([^\"]*)\"", "RED").toUpperCase(Locale.ROOT));
            bossbarSchematic.barStyle = BarStyle.valueOf(getContent(original, "bar_style=\"([^\"]*)\"", "SOLID").toUpperCase(Locale.ROOT));
            bossbarSchematic.update = Boolean.parseBoolean(getContent(original, "update=\"([^\"]*)\"", "false"));
            FlarePowered.LIB.getBossBarUtils().addBossBarToPlayer(player,
                    getContent(original, "id=\"([^\"]*)\"", String.valueOf(System.currentTimeMillis())),
                    bossbarSchematic);
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }

    private String getContent(String s, String pattern, String trowIfFailed) {
        Matcher matcher = Pattern.compile(pattern).matcher(s);
        if(matcher.find()) {
            return matcher.group(1);
        }
        return trowIfFailed;
    }

}