package net.flarepowered.neo.ui.contents;

import lombok.Setter;
import net.flarepowered.FlarePowered;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.neo.ui.items.FlareStack;
import net.flarepowered.other.Logger;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class InventoryScreen {

    private HashMap<Integer, ItemDock> content = new HashMap<>();
    private UI ui;

    public InventoryScreen(UI ui) {
        this.ui = ui;
    }

    public void updateUI (UI ui) {
        this.ui = ui;
    }

    public void wrapItems(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            if(section.contains(key + ".type")) {
                switch (section.getString(key + ".type").toLowerCase()) {
                    case "take":
                    case "out":
                    case "pickup":
                        ItemDock id = new ItemDock();
                        id.setToPickUpSlot();
                        if(section.contains(key + ".drop_item")) id.setDrop(section.getBoolean(key + ".drop_item"));
                        if(section.contains(key + ".on_take_commands")) id.setOnTakeCommands(section.getStringList(key + ".on_take_commands"));
                        if(section.contains(key + ".on_place_commands")) id.setOnPlaceCommands(section.getStringList(key + ".on_place_commands"));
                        if(section.contains(key + ".while_items_are_inside")) id.setWhileItemsAreInsideCommands(section.getStringList(key + ".while_items_are_inside"));
                        content.put(section.getInt(key + ".slot"), id);
                        continue;
                    case "input":
                    case "place":
                    case "dropoff":
                        ItemDock id1 = new ItemDock();
                        id1.setToDropOffSlot();
                        if(section.contains(key + ".drop_item")) id1.setDrop(section.getBoolean(key + ".drop_item"));
                        if(section.contains(key + ".on_take_commands")) id1.setOnTakeCommands(section.getStringList(key + ".on_take_commands"));
                        if(section.contains(key + ".on_place_commands")) id1.setOnPlaceCommands(section.getStringList(key + ".on_place_commands"));
                        if(section.contains(key + ".while_items_are_inside")) id1.setWhileItemsAreInsideCommands(section.getStringList(key + ".while_items_are_inside"));
                        content.put(section.getInt(key + ".slot"), id1);
                        continue;
                }
            }
            try {
                FlareStack flareStack = FlareStack.wrap(section.getConfigurationSection(key));
                if (section.contains(key + ".slot")) {
                    String value = section.getString( key + ".slot");
                    if(value.contains("-")) {
                        String[] values = value.split("-");
                        try {
                            for(int b = Integer.parseInt(values[0]); b <= Integer.parseInt(values[1]); b++)
                                addItemToDock(b, flareStack);
                        } catch (Exception e) {
                            Logger.error("You tried to put " + value + " as a slot range but it failed.");
                        }
                    } else addItemToDock(Integer.parseInt(value), flareStack);
                }
                if (section.contains(key + ".slots"))
                    for(int auto : section.getIntegerList(key + ".slots"))
                        addItemToDock(auto, flareStack);
            } catch (NumberFormatException e) {
                Logger.error("The item from " + section.getCurrentPath() + " has an invalid slots numbers, error code: " + e.getMessage());
            }
        }
    }

    public void addItemToDock(int slot, FlareStack item) {
        if(!content.containsKey(slot))
            content.put(slot, new ItemDock());
        content.get(slot).addItem(item);
    }

    public void update(Player player, Replace... replaces) {
        content.forEach((slot, dock) -> {
            try {
                if(dock.getDockType().equals(ItemDock.DockType.NORMAL_ITEM)) {
                    FlareStack stack = dock.neoGetItem(player, replaces);
                    if(stack != null) {
                        ui.getInventory().setItem(slot, stack.construct(player, replaces));
                        dock.getCurrent().onUpdate(player, replaces);
                    }
                } else if(dock.getDockType().equals(ItemDock.DockType.DROPOFF_SLOT))
                    dock.whileItemsAreInside(player, ui, slot, replaces);
                else if(dock.getDockType().equals(ItemDock.DockType.PICKUP_SLOT))
                    if(dock.isDockEmpty()) {
                        if (ui.getInventory().getItem(slot) != null)
                            ui.getInventory().setItem(slot, null);
                    } else
                        ui.getInventory().setItem(slot, dock.resetAndGetFirstItem().construct(player, replaces));
            } catch (ItemBuilderConfigurationException e) {
                Logger.warn("We skiped an item from loading (slot: " + slot + "). Error: " + e.getMessage());
            }
        });
    }

    public void construct(Player player, Replace... replaces) {
        ui.getInventory().clear();
        content.forEach((slot, dock) -> {
            try {
                if(dock.getDockType().equals(ItemDock.DockType.NORMAL_ITEM)) {
                    FlareStack stack = dock.neoGetItem(player, replaces);
                    if(stack != null)
                        ui.getInventory().setItem(slot, stack.construct(player, replaces));
                    //ui.getInventory().setItem(slot, dock.resetAndGetFirstItem().construct(player, replaces));
                } else if(dock.getDockType().equals(ItemDock.DockType.PICKUP_SLOT) && !dock.isDockEmpty())
                    ui.getInventory().setItem(slot, dock.resetAndGetFirstItem().construct(player, replaces));
            } catch (ItemBuilderConfigurationException e) {
                Logger.warn("We skiped an item from loading (slot: " + slot + "). Error: " + e.getMessage());
            }
        });
    }

    public ItemDock getDock(int slot) {
        if(!content.containsKey(slot)) return null;
        return content.get(slot);
    }

    public void removeDock(int slot) {
        if(!content.containsKey(slot)) return;
        content.remove(slot);
        ui.getInventory().setItem(slot, null);
    }

    public void removeFromDock(int slot, int amount) {
        System.out.println(ui.getPlayerAddedItems().toString());
        if(ui.getPlayerAddedItems().containsKey(slot)) {
            int toRemove = ui.getPlayerAddedItems().get(slot).getAmount() - amount;
            System.out.println(ui.getPlayerAddedItems().get(slot).getAmount() - amount);
            if(toRemove <= 0) {
                ui.getInventory().setItem(slot, null);
                ui.getPlayerAddedItems().remove(slot);
                return;
            }
            ui.getPlayerAddedItems().get(slot).setAmount(toRemove);
            ui.getInventory().getItem(slot).setAmount(toRemove);
            return;
        }
        ItemDock id = getDock(slot);
        if(id == null) return;
        FlareStack stack = id.getCurrent();
        if(stack == null) return;
        int toRemove = stack.getAmount() - amount;
        if(toRemove <= 0) {
            id.removeStack(stack);
            return;
        }
        stack.setAmount(toRemove);
    }

    private void updateLater (int slot, ItemDock id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ui.getInventory().setItem(slot, id.getCurrent().construct(ui.getPlayer(), ui.getReplaces()));
                } catch (ItemBuilderConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(FlarePowered.LIB.getPlugin(), 5);
    }

    public void addDock(int slot, FlareStack stack) {
        ItemDock id = getDock(slot);
        if(id == null) return;
        if(!content.containsKey(slot))
            content.put(slot, new ItemDock());
        if(isSimilar(id.getCurrent(), stack)) {
            try {
                id.getCurrent().setAmount(stack.getAmount() + id.getCurrent().getAmount());
                ui.getInventory().setItem(slot, id.getCurrent().construct(ui.getPlayer(), ui.getReplaces()));
            } catch (ItemBuilderConfigurationException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        content.get(slot).addItem(stack);
        try {
            ui.getInventory().setItem(slot, id.neoGetItem(ui.getPlayer(), ui.getReplaces()).construct(ui.getPlayer(), ui.getReplaces()));
        } catch (ItemBuilderConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSimilar(FlareStack ih1, FlareStack ih2) {
        if(ih1 == null || ih2 == null) return false;
        if(ih1.getDisplayName() != null && ih2.getDisplayName() != null) if(!ih1.getDisplayName().equals(ih2.getDisplayName())) return false;
        if(ih1.getMaterial() != null && ih2.getMaterial() != null) if(!ih1.getMaterial().toString().equals(ih2.getMaterial().toString())) return false;
        if(ih1.getLore() != null && ih2.getLore() != null) if(!ih1.getLore().equals(ih2.getLore())) return false;
        return true;
    }

}
