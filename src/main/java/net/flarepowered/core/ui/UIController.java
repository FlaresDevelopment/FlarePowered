package net.flarepowered.core.ui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.UUID;

public class UIController {

    private HashMap<UUID, UI> UI_HOLDER = new HashMap<>();

    public HashMap<UUID, UI> getUI_HOLDER() {
        return UI_HOLDER;
    }

    public void addToUI(UUID UUID, UI UI_HOLDER) {
        this.UI_HOLDER.put(UUID, UI_HOLDER);
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        
    }

}
