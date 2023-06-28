package net.flarepowered.core.menus.objects.items;

import com.cryptomorin.xseries.XMaterial;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlareItemMaterial {

    public FlareItemType type;
    public String content;

    public void getFromConfiguration(Configuration config, String path) throws ItemBuilderConfigurationException {
        if(config.contains(path + ".material")) {
            String rawMaterial = config.getString(path + ".materal");
            if(rawMaterial.matches("\\[(.*)]\\s?(.*)")) {
                Matcher matcher = Pattern.compile("\\[(.*)]\\s?(.*)").matcher(rawMaterial);
                if(!matcher.find())
                    throw new ItemBuilderConfigurationException("The item from " + path + " has a null material.");
                switch (matcher.group(1).toLowerCase(Locale.ROOT)) {
                    case "head":
                        this.content = matcher.group(2);
                        this.type = FlareItemType.BASE64_HEAD;
                    case "player":
                        this.content = matcher.group(2);
                        this.type = FlareItemType.PLAYER_HEAD;
                        break;
                    case "items_adder":
                        this.content = matcher.group(2);
                        this.type = FlareItemType.ITEMS_ADDER;
                        break;
                    case "executable_items":
                        this.content = matcher.group(2);
                        this.type = FlareItemType.ITEMS_EXECUTABLE;
                        break;
                    case "AIR":
                        this.content = matcher.group(2);
                        this.type = FlareItemType.EMPTY;
                        break;
                }
            } else {
                if(XMaterial.valueOf(rawMaterial) == null)
                    throw new ItemBuilderConfigurationException("The item from " + path + " has a invalid material.");
                this.content = rawMaterial;
                this.type = FlareItemType.NORMAL;
            }
        }
        else throw new ItemBuilderConfigurationException("The item from " + path + " has no display name.");
    }

    public ItemStack construct() {
        switch (type) {
            case NORMAL:
                return new ItemStack(XMaterial.matchXMaterial(content).get().parseMaterial());
        }
        return null;
    }

    public enum FlareItemType {
        NORMAL,
        EMPTY,
        BASE64_HEAD,
        PLAYER_HEAD,
        ITEMS_ADDER,
        ITEMS_EXECUTABLE,
        LIGHT_BULB,
        LEATHER_COLOR,
        HORSE_LEATHER_COLOR,
    }

}
