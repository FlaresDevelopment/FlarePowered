package net.flarepowered.neo.ui.items;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import dev.lone.itemsadder.api.CustomStack;
import net.flarepowered.FlarePowered;
import net.flarepowered.utils.XSeries.XMaterial;
import net.flarepowered.core.text.Message;
import net.flarepowered.other.Logger;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import net.flarepowered.utils.DependencyManager;
import net.flarepowered.utils.HeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlareMaterial {

    private String content;
    private MaterialType type;

    public static FlareMaterial wrapFromString(String materialString) {
        FlareMaterial material = new FlareMaterial();
        Matcher mather = Pattern.compile("\\[([^]]*)]\\W?(.*)").matcher(materialString);
        if(mather.find()) {
            String content = mather.group(2);
            switch (mather.group(1).toLowerCase(Locale.ROOT)) {
                case "base64":
                case "head":
                    material.setBase64Head(content);
                    break;
                case "player_head":
                    material.setPlayerHead(content);
                    break;
                case "ia":
                case "itemsadder":
                    material.setItemsAdder(content);
                    break;
                case "ie":
                case "executableitems":
                    material.setExecutableItems(content);
                    break;
                case "empty":
                    material.setEmpty();
                    break;
                case "potion":
                    material.setPotion(content);
                    break;
                default:
                    Logger.error("We cannot process the material (" + materialString + "). Please check the wiki for more help!");
                    break;
            }
        } else material.setMaterial(materialString);
        return material;
    }


    public ItemStack construct(Player player) throws ItemBuilderConfigurationException {
        switch (type) {
            case MATERIAL:
                if (!XMaterial.matchXMaterial(this.content).isPresent())
                    throw new ItemBuilderConfigurationException("The item that you tried to show has an unknown material (" + content + "). We are skipping this item");
                return XMaterial.matchXMaterial(this.content).get().parseItem();
            case POTION:
                ItemStack is = XMaterial.POTION.parseItem();
                PotionMeta potionMeta = (PotionMeta) is.getItemMeta();
                if(PotionEffectType.getByName(content) == null)
                    throw new ItemBuilderConfigurationException("The item has a null potion effect! (" + content + ").");
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(content), 1, 1), true);
                is.setItemMeta(potionMeta);
                return is;
            case BASE64_HEAD:
                return HeadUtils.getHeadFromBase64(content);
            case PLAYER_HEAD:
                return HeadUtils.getHeadFromName(Bukkit.getOfflinePlayer(Message.format(content, player)));
            case ITEMS_ADDER:
                if (FlarePowered.LIB.getPlugin().getServer().getPluginManager().getPlugin("ItemsAdder") == null)
                    throw new ItemBuilderConfigurationException("The items you tried to show is trying to get an item from ItemsAdder, but the plugin is not loaded or is not installed.");
                else {
                    CustomStack stack = CustomStack.getInstance(this.content);
                    if (stack != null)
                        return stack.getItemStack();
                    FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The item you tried to show is null, ItemsAdder can't find the item.");
                }
                break;
            case EXECUTABLE_ITEMS:
                if (!DependencyManager.GET.isPluginLoaded(DependencyManager.Dependency.ExecutableItems))
                    throw new ItemBuilderConfigurationException("The items you tried to show is trying to get an item from ExecutableItems, but the plugin is not loaded or is not installed.");
                else if (!ExecutableItemsAPI.getExecutableItemsManager().isValidID(this.content))
                    throw new ItemBuilderConfigurationException("The item you tried to show is null, ItemsAdder cant find the item.");
                else {
                    Optional<ExecutableItemInterface> eiOptional = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(this.content);
                    if (eiOptional.isEmpty()) {
                        throw new ItemBuilderConfigurationException("Ups.. ExecutableItems implementation went wrong :(");
                    }
                    return eiOptional.get().buildItem(1, Optional.empty());
                }
            case EMPTY:
                return XMaterial.AIR.parseItem();
        }
        throw new ItemBuilderConfigurationException("The material for the item could not be processed!");
    }

    public static FlareMaterial getFromItemStack(ItemStack item) {
        FlareMaterial materialStructure = new FlareMaterial();
        if (DependencyManager.GET.isPluginLoaded(DependencyManager.Dependency.ItemsAdder)) {
            CustomStack stack = CustomStack.byItemStack(item);
            if (stack != null) {
                return materialStructure.setItemsAdder(stack.getId());
            }
        }
        return item.getType().equals(XMaterial.PLAYER_HEAD.parseItem().getType()) ? materialStructure.setBase64Head(HeadUtils.getBase64FromHead(item)) : materialStructure.setMaterial(item.getType().toString());
    }

    public String toString() {
        switch (type) {
            case MATERIAL:
                return content;
            case BASE64_HEAD:
                return "[HEAD] " + content;
            case ITEMS_ADDER:
                if (FlarePowered.LIB.getPlugin().getServer().getPluginManager().getPlugin("ItemsAdder") == null) {
                    FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The items you tried to show is trying to get an item from ItemsAdder, but the plugin is not loaded or is not installed.");
                }

                CustomStack stack = CustomStack.getInstance(this.content);
                if (stack != null) {
                    return "[ItemsAdder] " + this.content;
                } else {
                    FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The item you tried to show is null, ItemsAdder cant find the item.");
                }
            case PLAYER_HEAD:
                return "[PLAYER] " + this.content;
            case EXECUTABLE_ITEMS:
            default:
                FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The item was null, you have a type: " + this.content + " and a key: " + this.content);
                return "";
        }
    }

    public FlareMaterial setMaterial(String material) {
        this.type = MaterialType.MATERIAL;
        this.content = material;
        return this;
    }

    public FlareMaterial setBase64Head(String base64) {
        this.type = MaterialType.BASE64_HEAD;
        this.content = base64;
        return this;
    }

    public FlareMaterial setPlayerHead(String username) {
        this.type = MaterialType.PLAYER_HEAD;
        this.content = username;
        return this;
    }

    public FlareMaterial setItemsAdder(String id) {
        this.type = MaterialType.ITEMS_ADDER;
        this.content = id;
        return this;
    }

    public FlareMaterial setExecutableItems(String ie) {
        this.type = MaterialType.EXECUTABLE_ITEMS;
        this.content = ie;
        return this;
    }

    public FlareMaterial setPotion(String potion) {
        this.type = MaterialType.POTION;
        this.content = potion;
        return this;
    }

    public FlareMaterial setEmpty() {
        this.type = MaterialType.EMPTY;
        this.content = "";
        return this;
    }

    public boolean isNormalItem() {
        return (type == MaterialType.EXECUTABLE_ITEMS || type == MaterialType.EMPTY || type == MaterialType.ITEMS_ADDER);
    }

    public enum MaterialType {
        MATERIAL,
        BASE64_HEAD,
        PLAYER_HEAD,
        ITEMS_ADDER,
        EXECUTABLE_ITEMS,
        HORSE_ARMOR,
        POTION,
        LIGHT_BLOCK,
        ENCHANTED_BOOK,
        LEATHER_ARMOR,
        EMPTY,
    }

}
