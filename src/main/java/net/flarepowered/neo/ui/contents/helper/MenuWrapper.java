package net.flarepowered.neo.ui.contents.helper;

import net.flarepowered.neo.ui.contents.InventoryScreen;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class MenuWrapper {

    public static HashMap<Integer, InventoryScreen> wrapFromConfig(String menuName, ConfigurationSection section) {
        HashMap<Integer, InventoryScreen> work = new HashMap<>();
        return work;
    }

}
