package net.flarepowered.neo.ui.contents;

import lombok.Getter;
import lombok.Setter;
import net.flarepowered.core.text.Message;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.neo.ui.contents.helper.MenuVariable;
import net.flarepowered.neo.ui.contents.helper.UITemplate;
import net.flarepowered.neo.ui.items.FlareStack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;

@Getter
@Setter
public class UI {

    private HashMap<Integer, InventoryScreen> content;
    private HashMap<Integer, ItemStack> playerAddedItems = new HashMap<>();
    private HashMap<String, MenuVariable> localVariables;
    private HashMap<String, Replace> replacesList = new HashMap<>();
    private int currentPage = 0;
    private Player player;

    private int size;
    private InventoryType type;
    private String title;

    private Inventory inventory;

    public static UI wrapMenuFromConfiguration(Player owner, String menuName, ConfigurationSection section) {
        UI ui = new UI();
        ui.player = owner;
        if(section.contains("size")) {
            ui.size = section.getInt("size");
        } else if(section.contains("type")) {
            ui.type = InventoryType.valueOf(section.getString("type"));
        }
        // Load variables
        if(section.contains("variables")) {
            ui.localVariables = new HashMap<>();
            for(String key : section.getConfigurationSection("variables").getKeys(false)) {
                if(!section.contains("variables." + key + ".type")) continue;
                if(!section.contains("variables." + key + ".value")) continue;
                switch (section.getString("variables." + key + ".type").toLowerCase(Locale.ROOT)) {
                    case "number":
                        ui.localVariables.put(key, new MenuVariable<>(key, section.getInt("variables." + key + ".value")));
                        break;
                    case "string":
                        ui.localVariables.put(key, new MenuVariable<>(key, section.getString("variables." + key + ".value")));
                        break;
                    case "boolean":
                        ui.localVariables.put(key, new MenuVariable<>(key, section.getBoolean("variables." + key + ".value")));
                        break;
                }
                ui.replacesList.put(key, new Replace("%var_" + key + "%", ui.localVariables.get(key).getVariable().toString()));

            }
        }
        /* Content and Meta */
        ui.title = section.contains("menu_title") ? section.getString("menu_title") : "Default FlarePanel™ Title";
        ui.content = new HashMap<>();
        ui.content.put(0, new InventoryScreen(ui));
        ui.content.get(0).wrapItems(section.getConfigurationSection("items"));
        ui.inventory = ui.type != null ? Bukkit.createInventory(owner, ui.type, Message.format(ui.title, owner)) : Bukkit.createInventory(owner, ui.size, Message.format(ui.title, owner));
        return ui;
    }

    public static UI wrapFromTemplate(Player owner, UITemplate template) {
        UI ui = new UI();
        ui.player = owner;
        if(template.getSize() != 0) {
            ui.size = template.getSize();
        } else if(template.getType() != null) {
            ui.type = template.getType();
        }
        // Load variables
        if(template.getReplacesList() != null) {
            ui.replacesList = (HashMap<String, Replace>) template.getReplacesList().clone();
        }
        if(template.getLocalVariables() != null) {
            for(MenuVariable key : template.getLocalVariables().values())
                ui.replacesList.put(key.getID(), new Replace("%var_" + key + "%", key.getVariable().toString()));
            ui.localVariables = template.getLocalVariables();
        }
        /* Content and Meta */
        ui.title = template.getTitle() != null ? template.getTitle() : "Default FlarePanel™ Title";
        ui.content = (HashMap<Integer, InventoryScreen>) template.getContent().clone();
        template.getContent().values().forEach(is -> is.updateUI(ui));
        ui.inventory = ui.type != null ? Bukkit.createInventory(owner, ui.type, Message.format(ui.title, owner)) : Bukkit.createInventory(owner, ui.size, Message.format(ui.title, owner));
        return ui;
    }

    public UITemplate exportPanel() {
        UITemplate template = new UITemplate();
        template.setSize(size);
        template.setTitle(title);
        template.setType(type);
        template.setContent(content);
        template.setLocalVariables(localVariables);
        template.setReplacesList(replacesList);
        return template;
    }

    /* UI Methods */

    public void render() {
        content.get(0).construct(player);
    }

    public void update() {
        content.get(currentPage).update(player, getReplaces());
    }

    public void showToPlayer() {
        render();
        update();
        player.openInventory(inventory);
    }

    public void click(int slot, ClickType clickType) {
        ItemDock id = content.get(currentPage).getDock(slot);
        if(id == null) return;
        if(id.isDockEmpty()) return;
        if(id.getCurrentIndex() == -1) return;
        id.getCurrent().onClick(player, clickType, getReplaces());
    }

    public void onItemPlace(int slot, ItemStack stack) {
        ItemDock id = content.get(currentPage).getDock(slot);
        if(id == null) return;
        id.onPlaceItem(player, stack, getReplaces());
    }

    public void onItemTake(int slot, ItemStack stack) {
        ItemDock id = content.get(currentPage).getDock(slot);
        if(id == null) return;
        id.onTakeItem(player, stack, getReplaces());
    }

    public boolean canInsert(int slot) {
        ItemDock id = content.get(currentPage).getDock(slot);
        if(id == null)
            return false;
        return id.getDockType() == ItemDock.DockType.DROPOFF_SLOT;
    }

    public boolean canTake(int slot) {
        ItemDock id = content.get(currentPage).getDock(slot);
        if(id == null)
            return false;
        return id.getDockType() == ItemDock.DockType.PICKUP_SLOT;
    }

    public boolean doDrop(int slot) {
        ItemDock id = content.get(currentPage).getDock(slot);
        if(id == null)
            return false;
        return id.isDrop();
    }

    public Replace[] getReplaces() {
        return this.replacesList.values().toArray(new Replace[0]);
    }

    public void removeDock(int slot) {
        content.get(currentPage).removeDock(slot);
    }

    public void removeItem(int slot) {
        content.get(currentPage).removeFromDock(slot, 128);
    }

    public void removeItem(int slot, int amount) {
        content.get(currentPage).removeFromDock(slot, amount);
    }

    public void addItem(int slot, FlareStack stack) {
        content.get(currentPage).addDock(slot, stack);
    }

}
