package net.flarepowered.core.menus.objects;

import net.flarepowered.core.menus.objects.items.FlareItem;
import net.flarepowered.core.text.StringUtils;
import net.flarepowered.utils.objects.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MenuRender {

    public Player owner;
    public HashMap<Integer, HashMap<Byte, Pair<FlareItem, Byte>>> inRender = new HashMap<>();
    public Inventory inventory;
    public int page;
    private MenuInterface menuInterface;
    private boolean updates;

    public MenuRender (Player owner, MenuInterface menuInterface, boolean async) {
        this.owner = owner;
        this.menuInterface = menuInterface;
        buildInventory();
        updateItems(false, async);
    }

    private void buildInventory() {
        if(inventory != null)
            return;
        if(!menuInterface.inventoryType.equals(InventoryType.CHEST))
            inventory = Bukkit.createInventory(owner, menuInterface.inventoryType, menuInterface.title);
        else
            inventory = Bukkit.createInventory(owner, menuInterface.menuSize, menuInterface.title);
    }

    public void createRender() {
        for(int page : menuInterface.items.keySet()) {
            if(!inRender.containsKey(page))
                inRender.put(page, new HashMap<>());
            for(byte slot : menuInterface.items.get(page).keySet()) {
                if(!inRender.get(page).containsKey(slot))
                    inRender.get(page).put(slot, new Pair<>());
                if(menuInterface.items.get(page).get(slot).size() == 1) {
                    if(!updates) updates = menuInterface.items.get(page).get(slot).get(0).update;
                    inRender.get(page).get(slot).first = processItemMeta(menuInterface.items.get(page).get(slot).get(0));
                    inRender.get(page).get(slot).second = 0;
                    inventory.setItem(slot, inRender.get(page).get(slot).first.construct());
                } else if(menuInterface.items.get(page).get(slot).size() > 1) {
                    int index = inRender.get(page).get(slot).second;
                    if(index >= menuInterface.items.get(page).get(slot).size() - 1) index = 0; else index++;
                    inRender.get(page).get(slot).first = processItemMeta(menuInterface.items.get(page).get(slot).get(index));
                    inventory.setItem(slot, inRender.get(page).get(slot).first.construct());
                }
            }
        }
    }

    public void updateItems(boolean onlyText, boolean async) {
        if(onlyText) createRender();
        for(byte slot : inRender.get(page).keySet()) {
            if(inRender.get(page).get(slot).first.update) {
                inRender.get(page).get(slot).first = processItemMeta(menuInterface.items.get(page).get(slot).get(inRender.get(page).get(slot).second));
                inventory.setItem(slot, inRender.get(page).get(slot).first.construct());
            }
        }
    }

    public void renderToPlayer() {
        owner.openInventory(inventory);
    }

    public FlareItem processItemMeta(FlareItem item) {
        if(item.displayName != null)
            item.displayName = StringUtils.formatMessage(item.displayName, owner);
        if (item.lore != null)
            item.lore = item.lore.stream()
                    .map(lore -> StringUtils.formatMessage(lore, owner))
                    .collect(Collectors.toCollection(ArrayList::new));
        if(item.clickCommands != null)
            item.lore = item.clickCommands.stream()
                    .map(click -> StringUtils.formatMessage(click, owner))
                    .collect(Collectors.toCollection(ArrayList::new));
        if(item.onUpdateCommands != null)
            item.lore = item.onUpdateCommands.stream()
                    .map(onUpdate -> StringUtils.formatMessage(onUpdate, owner))
                    .collect(Collectors.toCollection(ArrayList::new));
        if(item.onItemPlaceCommands != null)
            item.lore = item.onItemPlaceCommands.stream()
                    .map(onItem -> StringUtils.formatMessage(onItem, owner))
                    .collect(Collectors.toCollection(ArrayList::new));
        return item;
    }

}
