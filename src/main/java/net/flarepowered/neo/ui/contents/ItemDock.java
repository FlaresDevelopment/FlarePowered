package net.flarepowered.neo.ui.contents;

import lombok.Getter;
import lombok.Setter;
import net.flarepowered.FlarePowered;
import net.flarepowered.core.TML.FlareScript;
import net.flarepowered.core.text.Message;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.neo.ui.items.FlareStack;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemDock {

    private List<FlareStack> dockedItems = new ArrayList<>();

    @Getter @Setter
    private List<String> onPlaceCommands = new ArrayList<>();
    @Getter @Setter
    private List<String> onTakeCommands = new ArrayList<>();
    @Getter @Setter
    private List<String> whileItemsAreInsideCommands = new ArrayList<>();

    @Getter private int currentIndex;
    @Getter private int index;
    @Getter private boolean update;
    @Getter private DockType dockType = DockType.NORMAL_ITEM;
    @Getter @Setter
    private boolean drop = true;

    public void addItem(FlareStack stack) {
        dockedItems.add(stack);
    }

    public void setToDropOffSlot() {
        dockType = DockType.DROPOFF_SLOT;
    }

    public void setToPickUpSlot() {
        dockType = DockType.PICKUP_SLOT;
    }

    public FlareStack getCurrent() {
        if(isDockEmpty()) return null;
        if(dockedItems.size() == 1) return dockedItems.get(0);
        if(currentIndex >= dockedItems.size()) currentIndex = 0;
        return dockedItems.get(currentIndex);
    }

    public FlareStack neoGetItem(Player player, Replace... replaces) {
        if(index >= dockedItems.size()) index = 0;
        while(index < dockedItems.size()) {
            if (dockedItems.get(index).onView(player, replaces)) { currentIndex = index; return dockedItems.get(index++); }
            index++;
        }
        return null;
    }

    public FlareStack resetAndGetFirstItem() {
        if(isDockEmpty()) return null;
        if(dockedItems.size() == 1)
            return dockedItems.get(0);
        currentIndex = 0;
        return dockedItems.get(currentIndex);
    }

    public FlareStack setIndexAndGet(int index) {
        if(isDockEmpty()) return null;
        if(dockedItems.size() == 1) return dockedItems.get(0);
        if(index >= dockedItems.size()) return resetAndGetFirstItem();
        currentIndex = index;
        return dockedItems.get(currentIndex++);
    }

    public void whileItemsAreInside(Player player, UI ui, int slot, Replace... replace) {
        if(whileItemsAreInsideCommands == null)
            return;
        if(!ui.getPlayerAddedItems().containsKey(slot)) return;
        ItemMeta meta = ui.getPlayerAddedItems().get(slot).getItemMeta();
        assert meta != null;
        Replace[] temp = { new Replace("%item_display_name%", meta.hasDisplayName() ? meta.getDisplayName() : capitalize(ui.getPlayerAddedItems().get(slot).getType().name().toLowerCase(Locale.ROOT))),
                new Replace("%item_material%", ui.getPlayerAddedItems().get(slot).getType().name()) };
        new BukkitRunnable() {
            @Override
            public void run() {
                new FlareScript().processFull(Message.format(whileItemsAreInsideCommands, player, ArrayUtils.addAll(replace, temp)), player);
            }
        }.runTaskLater(FlarePowered.LIB.getPlugin(), 1L);
    }

    public void onPlaceItem(Player player, ItemStack stack, Replace... replace) {
        if(onPlaceCommands == null)
            return;
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        Replace[] temp = { new Replace("%item_display_name%", meta.hasDisplayName() ? meta.getDisplayName() : capitalize(stack.getType().name().toLowerCase(Locale.ROOT))),
                new Replace("%item_material%", stack.getType().name()) };
        new BukkitRunnable() {
            @Override
            public void run() {
                new FlareScript().processFull(Message.format(onPlaceCommands, player, ArrayUtils.addAll(replace, temp)), player);
            }
        }.runTaskLater(FlarePowered.LIB.getPlugin(), 1L);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void onTakeItem(Player player, ItemStack stack, Replace... replace) {
        if(onTakeCommands == null)
            return;
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        Replace[] temp = { new Replace("%item_display_name%", meta.getDisplayName()), new Replace("%item_material%", stack.getType().name()) };
        new FlareScript().processFull(Message.format(onTakeCommands, player, ArrayUtils.addAll(replace, temp)), player);
    }

    public int getDockSize() {
        return dockedItems.size();
    }

    public boolean isDockEmpty() {
        return dockedItems.isEmpty();
    }

    public void removeStack(FlareStack stack) {
        dockedItems.remove(stack);
    }

    public void removeStack(int position) {
        dockedItems.remove(position);
    }

    public enum DockType {
        NORMAL_ITEM,
        PICKUP_SLOT,
        DROPOFF_SLOT
    }

}
