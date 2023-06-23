package net.flarepowered.core.ui.objects;

import net.flarepowered.core.text.StringUtils;
import net.flarepowered.core.ui.item.ItemStructure;
import net.flarepowered.other.Logger;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PanelUI {

    String name;
    int panelSize;
    boolean view = true;
    InventoryType inventoryType;
    Inventory inventory;
    Player holder;
    HashMap<Integer, Slot> content;

    public PanelUI (Player holder, String name, int panelSize) {
        this.holder = holder;
        this.name = name;
        this.panelSize = panelSize;
    }

    public PanelUI (Player holder, String name, InventoryType inventoryType) {
        this.holder = holder;
        this.name = name;
        this.inventoryType = inventoryType;
    }

    public void injectContent(int slot, ItemStructure itemStructure) {
        if(content.containsKey(slot)) {
            content.get(slot).addContent(itemStructure);
            return;
        }
        Slot menuItem = new Slot();
        menuItem.addContent(itemStructure);
        content.put(slot, menuItem);
    }

    public void showToPlayer() {
        if(inventory == null)
            createUI();
        ItemStack[] itemsToLoad = new ItemStack[100];
        content.forEach((integer, menuSlots) -> {
            try { itemsToLoad[integer] = menuSlots.getItemStructure().construct(); } catch (ItemBuilderConfigurationException e) { Logger.error(e.getMessage()); }
        });
        inventory.setContents(itemsToLoad);
    }

    private void createUI () {
        if(inventoryType != null)
            inventory = Bukkit.createInventory(null, inventoryType, StringUtils.formatMessage(name, holder));
        else
            inventory = Bukkit.createInventory(null, panelSize, StringUtils.formatMessage(name, holder));
    }

    public void updateUI () {
        
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }
}
