package net.flarepowered.core.menus.objects;

import net.flarepowered.core.menus.objects.items.FlareItem;
import net.flarepowered.core.menus.other.ItemType;
import net.flarepowered.core.text.StringUtils;
import net.flarepowered.other.Logger;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import net.flarepowered.utils.objects.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MenuRender {

    public Player owner;
    public HashMap<Integer, HashMap<Byte, Pair<FlareItem, Byte>>> inRender = new HashMap<>();
    public HashMap<Integer, FlareItem> itemsHold;
    public Inventory inventory;
    public int page;
    public final MenuInterface menuInterface;
    private boolean updates;

    public MenuRender (Player owner, MenuInterface menuInterface, boolean async) {
        this.owner = owner;
        this.menuInterface = menuInterface;
        buildInventory();
        createRender();
        updateItems(true, async, true);
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
        try {
            if (!inRender.containsKey(page))
                inRender.put(page, new HashMap<>());
            for (byte slot : menuInterface.items.get(page).keySet()) {
                if (!inRender.get(page).containsKey(slot))
                    inRender.get(page).put(slot, new Pair<>(null, (byte) 0));
                if (menuInterface.items.get(page).get(slot).size() == 1) {
                    if (!updates) updates = menuInterface.items.get(page).get(slot).get(0).update;
                    if(inRender.get(page).get(slot).first == null) {
                        inRender.get(page).get(slot).first = menuInterface.items.get(page).get(slot).get(0);
                        inRender.get(page).get(slot).second = (byte) 0;
                        if(showItemConditions(inRender.get(page).get(slot).first))
                            if(slot > menuInterface.menuSize - 1) {
                                owner.getInventory().setItem(menuInterface.menuSize + 35 - 9 >= slot ? slot - menuInterface.menuSize + 9 : slot - menuInterface.menuSize - 36 + 9, inRender.get(page).get(slot).first.construct(owner));
                            } else
                                inventory.setItem(slot, inRender.get(page).get(slot).first.construct(owner));
                    }
                } else if (menuInterface.items.get(page).get(slot).size() > 1) {
                    int index = inRender.get(page).get(slot).second;
                    if (index >= menuInterface.items.get(page).get(slot).size() - 1) index = 0;
                    else index++;
                    inRender.get(page).get(slot).second = (byte) index;
                    inRender.get(page).get(slot).first = menuInterface.items.get(page).get(slot).get(index);
                    if(showItemConditions(inRender.get(page).get(slot).first))
                        if(slot > menuInterface.menuSize - 1) {
                            owner.getInventory().setItem(slot - 39 + 9, inRender.get(page).get(slot).first.construct(owner));
                        } else
                            inventory.setItem(slot, inRender.get(page).get(slot).first.construct(owner));
                }
            }
        } catch (ItemBuilderConfigurationException e) {
            Logger.error(e.getMessage());
        }
    }

    public void updateItems(boolean withItems, boolean async, boolean ignoreUpdatePropriety) {
        if (withItems) createRender();
        for (byte slot : inRender.get(page).keySet()) {
            if (inRender.get(page).get(slot).first.update || ignoreUpdatePropriety) {
                inRender.get(page).get(slot).first = menuInterface.items.get(page).get(slot).get(inRender.get(page).get(slot).second);
                try {
                    if (showItemConditions(inRender.get(page).get(slot).first))
                        inventory.setItem(slot, inRender.get(page).get(slot).first.construct(owner));
                } catch (ItemBuilderConfigurationException e) {
                    Logger.error(e.getMessage());
                }
            }
        }
    }

    public void renderToPlayer() {
        owner.openInventory(inventory);
    }

    private boolean showItemConditions(FlareItem item) {
        if(!item.canBeViewed(owner))
            return false;
        if(!item.itemType.equals(ItemType.NORMAL))
            return false;
        return true;
    }

}
