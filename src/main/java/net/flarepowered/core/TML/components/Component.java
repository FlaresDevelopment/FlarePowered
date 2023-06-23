package net.flarepowered.core.TML.components;

import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

public interface Component {

    TMLState run(String string, Player player) throws ComponentException;

}
