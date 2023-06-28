package net.flarepowered.core.menus.interfaces;

import org.bukkit.event.inventory.InventoryType;

import javax.swing.text.Element;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Menu {
    String menu_name();
    String menu_title() default "FlareMenu By FlarePowered";
    byte size() default 27;
    InventoryType menu_type() default InventoryType.CHEST;
    boolean update() default false;
}
