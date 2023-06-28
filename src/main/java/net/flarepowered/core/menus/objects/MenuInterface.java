package net.flarepowered.core.menus.objects;

import net.flarepowered.core.menus.objects.items.FlareItem;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuInterface {

    public String title;
    public byte menuSize;
    public InventoryType inventoryType;

    // PAGE - SLOT - ITEM
    public HashMap<Integer, HashMap<Byte, List<FlareItem>>> items;

    public void assignItem(int page, byte slot, FlareItem item) {
        //FIXME this could not work, not sure.
        if (items == null)
            items = new HashMap<>();
        if (items.containsKey(page)) {
            HashMap<Byte, List<FlareItem>> pageContent = items.get(page);
            if (!pageContent.containsKey(slot)) {
                pageContent.put(slot, new ArrayList<>());
                pageContent.get(slot).add(item);
            } else {
                items.put(page, new HashMap<>());
                items.get(page).put(slot, new ArrayList<>());
                items.get(page).get(slot).add(item);
            }
        }
    }
}
