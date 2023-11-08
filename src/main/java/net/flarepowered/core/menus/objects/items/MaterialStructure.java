package net.flarepowered.core.menus.objects.items;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import dev.lone.itemsadder.api.CustomStack;
import net.flarepowered.FlarePowered;
import net.flarepowered.core.menus.objects.XMaterial;
import net.flarepowered.core.text.Message;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import net.flarepowered.utils.DependencyManager;
import net.flarepowered.utils.HeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaterialStructure {

    public static MaterialStructure getFromString(String mat, Player player) {
        MaterialStructure materialStructure = new MaterialStructure();
        if(mat.matches("(?i)\\[(head|username|player|base64|itemsadder|executableitems)\\] (.+)")) {
            Pattern pat = Pattern.compile("(?i)\\[(head|username|player|base64|itemsadder|executableitems)\\] (.+)");
            Matcher matcher1 = pat.matcher(mat);
            matcher1.find();
            switch (matcher1.group(1).toLowerCase(Locale.ROOT)) {
                case "head":
                    materialStructure.setToBase64(matcher1.group(2));
                    break;
                case "username":
                    materialStructure.setToPlayerName(Message.format(matcher1.group(2), player));
                    break;
                case "player":
                    materialStructure.setToPlayerName(player.getName());
                    break;
                case "itemsadder":
                    materialStructure.setItemsAdderItem(matcher1.group(2));
                    break;
                case "executableitems":
                    materialStructure.setExecutableItemsItem(matcher1.group(2));
                    break;
            }
        } else
            materialStructure.setMaterial(Message.format(mat, player));
        return materialStructure;
    }

    public static MaterialStructure fromMaterial(String material) throws ItemBuilderConfigurationException {
        MaterialStructure materialStructure = new MaterialStructure();
        materialStructure.setMaterial(material);
        return materialStructure;
    }

    /* MATERIAL STRUCTURE CODE ========================--------------------======================= */

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
     * @param id the item id
     */
    public MaterialStructure setExecutableItemsItem(String id) {
        this.materialString = id;
        this.materialType = MaterialType.EXECUTABLE_ITEMS;
        return this;
    }

    /**
     * This will take the item from ExecutableItems
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
            case EXECUTABLE_ITEMS:
                if(!DependencyManager.GET.isPluginLoaded(DependencyManager.Dependency.ExecutableItems))
                    throw new ItemBuilderConfigurationException("The items you tried to show is trying to get an item from ItemsAdder, but the plugin is not loaded or is not installed.");
                if(!ExecutableItemsAPI.getExecutableItemsManager().isValidID(materialString))
                    throw new ItemBuilderConfigurationException("The item you tried to show is null, ItemsAdder cant find the item.");
                Optional<ExecutableItemInterface> eiOptional = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(materialString);
                if(eiOptional.isEmpty())
                    throw new ItemBuilderConfigurationException("Ups.. ExecutableItems implementation went wrong :(");
                return eiOptional.get().buildItem(1, Optional.empty());
            case PLAYER_HEAD:
                return HeadUtils.getHeadFromName(Bukkit.getOfflinePlayer(materialString));
            default:
                throw new ItemBuilderConfigurationException("The item was null, you have a type: " + materialType + " and a key: " + materialString);
        }
    }

    public static MaterialStructure getFromItemStack(ItemStack item) {
        MaterialStructure materialStructure = new MaterialStructure();
        if(DependencyManager.GET.isPluginLoaded(DependencyManager.Dependency.ItemsAdder)) {
            CustomStack stack = CustomStack.byItemStack(item);
            if (stack != null)
                return materialStructure.setItemsAdderItem(stack.getId());
        }
        if(item.getType().equals(XMaterial.PLAYER_HEAD.parseItem().getType()))
            return materialStructure.setToBase64(HeadUtils.getBase64FromHead(item));
        return materialStructure.setMaterial(item.getType().toString());
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
        EXECUTABLE_ITEMS,
    }
}