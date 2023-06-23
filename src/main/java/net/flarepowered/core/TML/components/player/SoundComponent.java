package net.flarepowered.core.TML.components.player;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class SoundComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[sound] sound=(\\w+)\\s*(volume=(\\d+\\.?\\d*))?\\s*(pitch=(\\d+\\.?\\d*))?");

    //(\w+=(\S+))
    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [SOUND] has not sound defined, use [SOUND] sound=<sound>. We are skipping this item.");
            float pitch = 1f;
            float volume = 1f;
            if(matcher.group(3) != null)
                volume = Float.parseFloat(matcher.group(3));
            if(matcher.group(5) != null)
                pitch = Float.parseFloat(matcher.group(5));
            Sound sound = Sound.valueOf(matcher.group(1));
            player.playSound(player.getLocation(), sound, pitch, volume);
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
