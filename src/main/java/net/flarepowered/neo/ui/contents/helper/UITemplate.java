package net.flarepowered.neo.ui.contents.helper;

import lombok.Getter;
import lombok.Setter;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.neo.ui.contents.InventoryScreen;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;

@Setter
@Getter
public class UITemplate {
    private int size;
    private InventoryType type;
    private String title;
    private HashMap<String, MenuVariable> localVariables;
    private HashMap<Integer, InventoryScreen> content;
    private HashMap<String, Replace> replacesList = new HashMap<>();

}
