package net.flarepowered.core.menus;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.menus.interfaces.Menu;
import net.flarepowered.core.menus.interfaces.MenuItem;
import net.flarepowered.core.menus.interfaces.ReactiveMenuItem;
import net.flarepowered.core.menus.objects.items.FlareItem;
import net.flarepowered.core.menus.objects.MenuInterface;
import net.flarepowered.core.menus.objects.MenuRender;
import net.flarepowered.core.menus.other.ItemType;
import net.flarepowered.other.exceptions.MenuRenderException;
import net.flarepowered.utils.objects.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MenuManager implements Listener {

    public HashMap<String, MenuInterface> CACHED_MENUS = new HashMap<>();
    public HashMap<UUID, MenuRender> menusInRender = new HashMap<>();

    public void onEnable() {
        /* MAIN STUFF */
        FlarePowered.LIB.getPlugin().getServer().getPluginManager().registerEvents(this, FlarePowered.LIB.getPlugin());
        /* ANNOTATION SCANNING */
//        String packageName = this.getClass().getPackage().toString();
//
//        // Set up the Reflections library configuration
//        ConfigurationBuilder configBuilder = new ConfigurationBuilder()
//                .setScanners(new SubTypesScanner(false))
//                .filterInputsBy(new FilterBuilder().includePackage(packageName));
//
//        // Create a new Reflections instance with the configuration
//        Reflections reflections = new Reflections(configBuilder);
//
//        // Get all annotated classes in the package
//        List<Class<?>> annotatedClasses = new ArrayList<>(reflections.getSubTypesOf(Object.class));
//
//        // Process the annotated classes
//        for (Class<?> clazz : annotatedClasses) {
//            if (clazz.isAnnotationPresent(Menu.class)) {
//                try {
//                    CACHED_MENUS.put(clazz.getAnnotation(Menu.class).menu_name(), buildMenuByInterface(clazz));
//                } catch (InvocationTargetException | IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//        /* Starts menus engine */
        runner();
    }

    public void manualIndex(Object object) {
        if (object.getClass().isAnnotationPresent(Menu.class)) {
            try {
                CACHED_MENUS.put(object.getClass().getAnnotation(Menu.class).menu_name(), buildMenuByInterface(object));
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * You will use this method to create a menu interface using our
     * annotations api make sure that you will provide a class that is annotated
     * with @Menu from flaredevelopment
     * @param object
     * @return
     */
    public MenuInterface buildMenuByInterface(Object object) throws InvocationTargetException, IllegalAccessException {
        MenuInterface menuInterface = new MenuInterface();
        Menu menuAnnotation = object.getClass().getAnnotation(Menu.class);
        menuInterface.title = menuAnnotation.menu_name();
        if(menuAnnotation.menu_type().equals(InventoryType.CHEST)) {
            menuInterface.menuSize = menuAnnotation.size();
        } else {
            menuInterface.menuSize = -1;
        }
        menuInterface.inventoryType = menuAnnotation.menu_type();
        /* Construct the items */
        for(Method method : object.getClass().getMethods()) {
            if(method.isAnnotationPresent(MenuItem.class)) {
                Class<?> returnType = method.getReturnType();
                if (returnType == FlareItem.class) {
                    FlareItem item = (FlareItem) method.invoke(object, null);
                    menuInterface.assignItem(item.page, item);
                }
            } else if(method.isAnnotationPresent(ReactiveMenuItem.class)) {
                // code here
            }
        }
        return menuInterface;
    }


    /**
     * The runner will perform an update every 20 ticks (one second)
     * This operation in ASYNC but its will heavy to run
     *  - Optimize placeholders
     *  - TML
     */
    long lastTimeForItemsUpdate = 0;
    private void runner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Recheck views requirements
                if(lastTimeForItemsUpdate < System.currentTimeMillis()) {
                    for(MenuRender menu : menusInRender.values()) {
                        if(menu.itemUpdates)
                            menu.updateMenuItems(true, false);
                    }
                    lastTimeForItemsUpdate = System.currentTimeMillis() + 5000;
                }
                // Updates just items one time per second.
                for(MenuRender menu : menusInRender.values()) {
                    menu.updateMenuItems(false, false);
                }
            }
        }.runTaskTimerAsynchronously(FlarePowered.LIB.getPlugin(), 0, 8);
    }

    public void renderMenuToPlayer(Player player, String menuName) throws MenuRenderException {
        if (!this.CACHED_MENUS.containsKey(menuName))
            throw new MenuRenderException("Menu " + menuName + "dose not exist on the cached menus, try reloading if you have the menu created");
        menusInRender.put(player.getUniqueId(), new MenuRender(player, this.CACHED_MENUS.get(menuName), true));
        menusInRender.get(player.getUniqueId()).renderToPlayer();
    }

//    EVENTS =====================================

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        UUID player = event.getWhoClicked().getUniqueId();
        if(!menusInRender.containsKey(player))
            return;
        if(!menusInRender.get(player).inventory.equals(event.getInventory()))
            return;
        if(!menusInRender.get(player).menuInterface.enableInteractions)
            event.setCancelled(true);
        else if(event.getCursor() != null) {
            Pair<FlareItem, Byte> hold = menusInRender.get(player).inRender.get(menusInRender.get(player).page).get((byte) event.getRawSlot());
            if (!hold.first.itemType.equals(ItemType.CAN_PUT_ITEMS))
                event.setCancelled(true);
            else
                System.out.println(event.getCursor());
        }
        if(event.getRawSlot() <= 127 && event.getRawSlot() <= -127)
            return;
        if(!menusInRender.get(player).inRender.get(menusInRender.get(player).page).containsKey((byte) event.getRawSlot()))
            return;
        menusInRender.get(player).inRender.get(menusInRender.get(player).page).get((byte) event.getRawSlot()).first.onClickCommands((Player) event.getWhoClicked(), event.getClick());
    }

    @EventHandler
    private void onInventoryPlaceEvent(InventoryDragEvent event) {
        UUID player = event.getWhoClicked().getUniqueId();
        if(!menusInRender.containsKey(player))
            return;
        if(!menusInRender.get(player).inventory.equals(event.getInventory()))
            return;
        if(!menusInRender.get(player).menuInterface.enableInteractions)
            event.setCancelled(true);
    }

    @EventHandler
    private void inventoryClose(InventoryCloseEvent event) {
        UUID player = event.getPlayer().getUniqueId();
        if(!menusInRender.containsKey(player))
            return;
        if(!menusInRender.get(player).inventory.equals(event.getInventory()))
            return;
        menusInRender.remove(player);
    }

}
