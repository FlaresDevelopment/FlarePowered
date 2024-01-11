package net.flarepowered.neo.ui;

import lombok.Getter;
import net.flarepowered.FlarePowered;
import net.flarepowered.neo.ui.contents.UI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum MenuArchitect implements Listener {
    MENU;

    @Getter
    private HashMap<Player, UI> holder;

    public void onEnable() {
        holder = new HashMap<>();
        runner();
        Bukkit.getPluginManager().registerEvents(this, FlarePowered.LIB.getPlugin());
    }

    public void openInventory(Player player, UI ui) {
        holder.put(player, ui);
        ui.showToPlayer();
    }

    private void runner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Map.Entry<Player, UI> map : holder.entrySet()) {
                    if(!map.getKey().isOnline()) { holder.remove(map.getKey()); continue; }
                    map.getValue().update();
                }
            }
        }.runTaskTimerAsynchronously(FlarePowered.LIB.getPlugin(), 0L, 19L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryInteract(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if(!holder.containsKey(player)) return;
        if(event.getClickedInventory() == null) return;
        UI ui = holder.get(player);
        //if(!holder.get(player).getInventory().equals(event.getClickedInventory())) return;
        if(ui.getPlayerAddedItems().containsKey(event.getRawSlot()))
            if(removeActions.contains(event.getAction())) {
                ui.getPlayerAddedItems().remove(event.getRawSlot());
                return;
            }
        if(event.getClickedInventory().getType() == InventoryType.PLAYER) return;

        if(placeActions.contains(event.getAction()))
            if(ui.canInsert(event.getRawSlot())) {
                switch (event.getAction()) {
                    case PLACE_ALL:
                        ui.getPlayerAddedItems().put(event.getRawSlot(), event.getCursor().clone());
                        ui.onItemPlace(event.getRawSlot(), event.getCursor());
                        return;
                    case PLACE_ONE:
                        ui.getPlayerAddedItems().put(event.getRawSlot(), event.getCursor().clone());
                        ui.getPlayerAddedItems().get(event.getRawSlot()).setAmount(1);
                        ui.onItemPlace(event.getRawSlot(), ui.getPlayerAddedItems().get(event.getRawSlot()));
                        return;
                }
                return;
            }

        if(removeActions.contains(event.getAction())) {
            if(ui.canTake(event.getRawSlot())) {
                ui.onItemTake(event.getRawSlot(), event.getCurrentItem());
                switch (event.getAction()) {
                    case PICKUP_ALL:
                        ui.removeItem(event.getRawSlot());
                        return;
                    case PICKUP_HALF:
                        int temp = event.getCurrentItem().getAmount()/2;
                        ui.removeItem(event.getRawSlot(), event.getCurrentItem().getAmount() % 2 == 0 ? temp : temp + 1);
                        return;
                }
            }
        }

        ui.click(event.getRawSlot(), event.getClick());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(!holder.containsKey(player)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if(!holder.containsKey(player)) return;
        UI ui = holder.get(player);
        if(!ui.getPlayerAddedItems().isEmpty())
            ui.getPlayerAddedItems().forEach((key, value) -> { if(ui.doDrop(key)) player.getInventory().addItem(value); });
        holder.remove(player);
    }

    final List<InventoryAction> placeActions = Arrays.asList(InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME);
    final List<InventoryAction> removeActions = Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF);

}
