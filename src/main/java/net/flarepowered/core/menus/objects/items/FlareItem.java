package net.flarepowered.core.menus.objects.items;

import com.cryptomorin.xseries.XMaterial;
import net.flarepowered.FlarePowered;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import net.flarepowered.utils.VersionControl;
import net.flarepowered.utils.objects.Pair;
import org.bukkit.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlareItem {

    /* Menu parameters */
    public byte slot;
    public int page;
    public MenuItemState menuItemState = MenuItemState.NORMAL;
    public int priority;
    public boolean canBeViewed = true;
    public boolean update;
    public boolean reactive;
    public Pair<String, String> reactiveConfig;
    public List<String> clickCommands;
    public List<String> onItemPlaceCommands;
    public List<String> onUpdateCommands;

    /* Item meta */
    public FlareItemMaterial material;
    public String displayName;
    public List<String> lore;
    public int customModelData;
    /* Other */
    public Color horseColor;
    public boolean glow;

    public FlareItem getObject() throws ItemBuilderConfigurationException {
        if(!reactive)
            return this;
        getAutomaticallyFromConfig();
        return this;
    }

    private void getAutomaticallyFromConfig() throws ItemBuilderConfigurationException {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FlarePowered.LIB.getPlugin().getDataFolder(), reactiveConfig.second));
        String path = reactiveConfig.first;
        if(config.contains(path + ".display_name"))
            this.displayName = config.getString(path + ".display_name");
        else throw new ItemBuilderConfigurationException("The item from " + path + " has no display name.");
        material.getFromConfiguration(config, path);
    }

    public ItemStack construct() {
        ItemStack itemStack = material.construct();
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(displayName);
        im.setLore(lore);
        if(VersionControl.getVersion() > 13)
            im.setCustomModelData(customModelData);
        if(glow) {
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(im);
        return itemStack;
    }

    public enum MenuItemState {
        NORMAL,
        CAN_PUT_ITEM,
        CAN_TAKE_ITEM
    }
}
