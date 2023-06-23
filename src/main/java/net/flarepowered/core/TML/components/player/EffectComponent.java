package net.flarepowered.core.TML.components.player;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.regex.Matcher;

public class EffectComponent implements Component {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[effect] effect=(\\w+)\\s*(duration=(\\d+\\.?\\d*))?\\s*(amplifier=(\\d+\\.?\\d*))?");

    //(\w+=(\S+))
    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new ComponentException("The component [SOUND] has not sound defined, use [SOUND] sound=<sound>. We are skipping this item.");
            int duration = 1;
            int amplifier = 1;
            if(matcher.group(3) != null)
                duration = Integer.parseInt(matcher.group(3));
            if(matcher.group(5) != null)
                amplifier = Integer.parseInt(matcher.group(5));
            PotionEffectType effect = PotionEffectType.getByName(matcher.group(1));
            player.addPotionEffect(new PotionEffect(effect, duration, amplifier));
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }
}
