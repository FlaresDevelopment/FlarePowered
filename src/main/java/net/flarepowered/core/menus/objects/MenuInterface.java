package net.flarepowered.core.menus.objects;

import net.flarepowered.core.menus.objects.items.FlareItem;
import net.flarepowered.core.menus.other.ItemType;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MenuInterface {

    public String title;
    public byte menuSize;
    public InventoryType inventoryType;
    public boolean enableInteractions;
    // PAGE - SLOT - ITEM
    public HashMap<Integer, HashMap<Byte, LinkedList<FlareItem>>> items = new HashMap<>();

    public void assignItem(int page, FlareItem item) {
        if(item.itemType != ItemType.NORMAL)
            enableInteractions = true;
        // Getting slots (slot)
        item.slot.forEach(b -> {
            addToSlot(page, b, item);
        });
    }

    void addToSlot(int page, byte slot, FlareItem item) {
        if(!items.containsKey(page))
            items.put(page, new HashMap<>());
        if (!items.get(page).containsKey(slot)) {
            items.get(page).put(slot, new LinkedList<>());
            items.get(page).get(slot).addFirst(item);
        } else {
            items.get(page).get(slot).addFirst(item);
        }
    }

}
