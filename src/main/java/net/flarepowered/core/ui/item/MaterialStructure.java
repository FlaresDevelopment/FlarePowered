package net.flarepowered.core.ui.item;

import com.cryptomorin.xseries.XMaterial;
import dev.lone.itemsadder.api.CustomStack;
import net.flarepowered.FlarePowered;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import net.flarepowered.utils.HeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class MaterialStructure {

    public MaterialType materialType = null;
    public String materialString = null;

    public MaterialStructure() {}

    /**
     * This will create a player head with playername.
     * @param playerName The player name
     */
    public MaterialStructure setToPlayerName(String playerName) {
        this.materialString = playerName;
        this.materialType = MaterialType.PLAYER_HEAD;
        return this;
    }

    /**
     * This will create a player head with value (Base64).
     * @param base64_value The base64 value
     */
    public MaterialStructure setToBase64(String base64_value) {
        this.materialString = base64_value;
        this.materialType = MaterialType.BASE64_HEAD;
        return this;
    }

    /**
     * This will create a basic material item
     * @param material Spigot MC material
     */
    public MaterialStructure setMaterial(String material) {
        this.materialString = material;
        this.materialType = MaterialType.MATERIAL;
        return this;
    }

    /**
     * This will create a basic material item without XMaterial
     * @param material Spigot MC material
     */
    public MaterialStructure setMaterialNoXMaterial(String material) {
        this.materialString = material;
        this.materialType = MaterialType.MATERIAL;
        return this;
    }

    /**
     * This will take the item from ItemsAdder
     * @param namespaceId add the item namespace id
     */
    public MaterialStructure setItemsAdderItem(String namespaceId) {
        this.materialString = namespaceId;
        this.materialType = MaterialType.ITEMS_ADDER;
        return this;
    }

    public ItemStack construct() throws ItemBuilderConfigurationException {
        switch (materialType) {
            case MATERIAL:
                if(!XMaterial.matchXMaterial(materialString).isPresent())
                    throw new ItemBuilderConfigurationException("The item that you tried to show has an unknown material (" + materialString + "). We are skipping this item");
                return XMaterial.matchXMaterial(materialString).get().parseItem();
            case BASE64_HEAD:
                return HeadUtils.getHeadFromBase64(materialString);
            case ITEMS_ADDER:
                if(FlarePowered.LIB.getPlugin().getServer().getPluginManager().getPlugin("ItemsAdder") == null)
                    throw new ItemBuilderConfigurationException("The items you tried to show is trying to get an item from ItemsAdder, but the plugin is not loaded or is not installed.");
                CustomStack stack = CustomStack.getInstance(materialString);
                if(stack == null)
                    FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The item you tried to show is null, ItemsAdder cant find the item.");
                else
                    return stack.getItemStack();
            case PLAYER_HEAD:
                return HeadUtils.getHeadFromName(Bukkit.getOfflinePlayer(materialString));
            default:
                throw new ItemBuilderConfigurationException("The item was null, you have a type: " + materialType + " and a key: " + materialString);
        }
    }

    @Override
    public String toString() {
        switch (materialType) {
            case MATERIAL:
                if(!XMaterial.matchXMaterial(materialString).isPresent())
                    FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The item that you tried to show has an unknown material (" + materialString + "). We are skipping this item");
                return XMaterial.matchXMaterial(materialString).get().toString();
            case BASE64_HEAD:
                return "[HEAD] " + materialString;
            case ITEMS_ADDER:
                if(FlarePowered.LIB.getPlugin().getServer().getPluginManager().getPlugin("ItemsAdder") == null)
                    FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The items you tried to show is trying to get an item from ItemsAdder, but the plugin is not loaded or is not installed.");
                CustomStack stack = CustomStack.getInstance(materialString);
                if(stack == null)
                    FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The item you tried to show is null, ItemsAdder cant find the item.");
                else
                    return "[ItemsAdder] " + materialString;
            case PLAYER_HEAD:
                return "[PLAYER] " + materialString;
            default:
                FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, "The item was null, you have a type: " + materialType + " and a key: " + materialString);
        }
        return "";
    }

    public enum MaterialType {
        MATERIAL,
        PLAYER_HEAD,
        BASE64_HEAD,
        ITEMS_ADDER,
    }
}