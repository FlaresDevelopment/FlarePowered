package net.flarepowered.core.TML.check;

import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.CheckException;
import org.bukkit.entity.Player;

public interface Requirement {

    TMLState run(String string, Player player) throws CheckException;

}
