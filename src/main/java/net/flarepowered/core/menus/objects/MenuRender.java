package net.flarepowered.core.menus.objects;

import net.flarepowered.core.menus.objects.items.FlareItem;
import net.flarepowered.core.menus.other.ItemType;
import net.flarepowered.core.text.Message;
import net.flarepowered.other.Logger;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import net.flarepowered.utils.objects.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.LinkedList;

public class MenuRender {

    public Player owner;
    public HashMap<Integer, HashMap<Byte, Pair<FlareItem, Byte>>> inRender = new HashMap<>();
    public HashMap<Integer, FlareItem> itemsHold;
    public Inventory inventory;
    public int page;
    public final MenuInterface menuInterface;
    public boolean itemUpdates;

    public MenuRender (Player owner, MenuInterface menuInterface, boolean async) {
        this.owner = owner;
        this.itemUpdates = menuInterface.itemsUpdates;
        this.menuInterface = menuInterface;
        buildInventory();
        renderEngineV2();
        updateMenuItems(true, true);
    }

    private void buildInventory() {
        if(inventory != null)
            return;
        if(!menuInterface.inventoryType.equals(InventoryType.CHEST))
            inventory = Bukkit.createInventory(owner, menuInterface.inventoryType, Message.format(menuInterface.title, owner));
        else
            inventory = Bukkit.createInventory(owner, menuInterface.menuSize, Message.format(menuInterface.title, owner));
    }

    public void updateMenuItems(boolean reloadItems, boolean ignoreUpdatePropriety) {
        if (reloadItems) renderEngineV2();
        for (byte slot : inRender.get(page).keySet()) {
            if(inRender.get(page).get(slot).first == null)
                continue;
            if (inRender.get(page).get(slot).first.update || ignoreUpdatePropriety) {
                addToRender(menuInterface.items.get(page).get(slot).get(inRender.get(page).get(slot).second), slot, inRender.get(page).get(slot).second);
            }
        }
    }

    public void renderEngineV2() {
        if (!inRender.containsKey(page))
            inRender.put(page, new HashMap<>());
        for (byte slot : menuInterface.items.get(page).keySet()) {
            LinkedList<FlareItem> slotItems = menuInterface.items.get(page).get(slot);
            if(slotItems.size() == 1) {
                addToRender(slotItems.get(0), slot, (byte) 0);
            } else if(slotItems.size() > 1) {
                byte index = inRender.get(page).get(slot) != null ? inRender.get(page).get(slot).second : -1;
                do {
                    if (index >= slotItems.size() - 1) {
                        inRender.get(page).get(slot).second = (byte) 0;
                        break;
                    } else index++;
                    addToRender(slotItems.get(index), slot, index);
                } while (true);
            }
        }
    }

    private boolean addToRender(FlareItem item, byte slot, byte index) {
        if(!item.canBeViewed(owner)) {
            return false;
        }
        if(inRender.get(page).containsKey(slot) && inRender.get(page).get(slot) != null) {
            inRender.get(page).get(slot).first = item;
            inRender.get(page).get(slot).second = index;
        } else
            inRender.get(page).put(slot, new Pair<>(item, index));
        try {
            if(showItemConditions(item))
                if(slot > menuInterface.menuSize - 1) {
                    owner.getInventory().setItem(menuInterface.menuSize + 35 - 9 >= slot ? slot - menuInterface.menuSize + 9 : slot - menuInterface.menuSize - 36 + 9, item.construct(owner));
                } else {
                   inventory.setItem(slot, item.construct(owner));
                }
        } catch (ItemBuilderConfigurationException e) {
            Logger.error(e.getMessage());
        }
        return true;
    }

    @Deprecated
    public void createRender() {
        try {
            if (!inRender.containsKey(page))
                inRender.put(page, new HashMap<>());
            for (byte slot : menuInterface.items.get(page).keySet()) {
                if (!inRender.get(page).containsKey(slot))
                    inRender.get(page).put(slot, new Pair<>(null, (byte) 0));
                if (menuInterface.items.get(page).get(slot).size() == 1) {
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

    @Deprecated
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
        if(item.material.contains("[empty]"))
            return false;
        if(!item.itemType.equals(ItemType.NORMAL))
            return false;
        return true;
    }

}
