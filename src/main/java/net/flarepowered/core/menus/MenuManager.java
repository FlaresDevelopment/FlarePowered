package net.flarepowered.core.menus;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.menus.interfaces.Menu;
import net.flarepowered.core.menus.interfaces.MenuItem;
import net.flarepowered.core.menus.interfaces.ReactiveMenuItem;
import net.flarepowered.core.menus.objects.items.FlareItem;
import net.flarepowered.core.menus.objects.MenuInterface;
import net.flarepowered.core.menus.objects.MenuRender;
import net.flarepowered.other.exceptions.MenuRenderException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MenuManager {

    private HashMap<String, MenuInterface> CACHED_MENUS = new HashMap<>();
    public HashMap<UUID, MenuRender> menusInRender = new HashMap<>();

    public void onEnable() {
        /* ANNOTATION SCANNING */
        String packageName = this.getClass().getPackage().toString();

        // Set up the Reflections library configuration
        ConfigurationBuilder configBuilder = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().includePackage(packageName));

        // Create a new Reflections instance with the configuration
        Reflections reflections = new Reflections(configBuilder);

        // Get all annotated classes in the package
        List<Class<?>> annotatedClasses = new ArrayList<>(reflections.getSubTypesOf(Object.class));

        // Process the annotated classes
        for (Class<?> clazz : annotatedClasses) {
            if (clazz.isAnnotationPresent(Menu.class)) {
                try {
                    CACHED_MENUS.put(clazz.getAnnotation(Menu.class).menu_name(), buildMenuByInterface(clazz));
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        /* Starts menus engine */
        runner();
    }

    public void manualIndex(Object object) {
        if (object.getClass().isAnnotationPresent(Menu.class)) {
            try {
                CACHED_MENUS.put(object.getClass().getAnnotation(Menu.class).menu_name(), buildMenuByInterface(object.getClass()));
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The runner will perform an update every 20 ticks (one second)
     * This operation in ASYNC but its will heavy to run
     *  - Optimize placeholders
     *  - TML
     */
    private void runner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(MenuRender menu : menusInRender.values()) {
                    menu.updateItems(true, true);
                }
            }
        }.runTaskTimerAsynchronously(FlarePowered.LIB.getPlugin(), 0, 20);
    }

    /**
     * You will use this method to create a menu interface using our
     * annotations api make sure that you will provide a class that is annotated
     * with @Menu from flaredevelopment
     * @param clazz
     * @return
     */
    public MenuInterface buildMenuByInterface(Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        MenuInterface menuInterface = new MenuInterface();
        Menu menuAnnotation = clazz.getAnnotation(Menu.class);
        menuInterface.title = menuAnnotation.menu_name();
        if(menuAnnotation.menu_type().equals(InventoryType.CHEST))
            menuInterface.menuSize = menuAnnotation.size();
        else {
            menuInterface.menuSize = -1;
            menuInterface.inventoryType = menuAnnotation.menu_type();
        }
        /* Construct the items */
        for(Method method : clazz.getMethods()) {
            if(method.isAnnotationPresent(MenuItem.class)) {
                Class<?> returnType = method.getReturnType();
                if (returnType == FlareItem.class) {
                    FlareItem item = (FlareItem) method.invoke(null);
                    menuInterface.assignItem(item.page, item.slot, item);
                }
            } else if(method.isAnnotationPresent(ReactiveMenuItem.class)) {
                // code here
            }
        }
        return menuInterface;
    }

    public void renderMenuToPlayer(Player player, String menuName) throws MenuRenderException {
        if(CACHED_MENUS.containsKey(menuName)) {

            return;
        }
        throw new MenuRenderException("Menu " + menuName + "dose not exist on the cached menus, " +
                "try reloading if you have the menu created");
    }

}
