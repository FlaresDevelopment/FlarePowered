package net.flarepowered.neo.ui.items;

import lombok.Getter;
import lombok.Setter;
import net.flarepowered.core.TML.FlareScript;
import net.flarepowered.core.text.Message;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.other.Logger;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import net.flarepowered.utils.VersionControl;
import net.flarepowered.utils.objects.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the neo FlareItem class, with improvements and new features
 * and support for pages, fast refresh caching, item place, item take,
 * empty items and priorities. There is better syntax and error handling.
 * Thing added in FlareStack are HorseColor, LightBlock, potions, enchants
 * and much more
 */

@Setter
@Getter
public class FlareStack {

    private FlareMaterial material;
    private String displayName;
    private List<String> lore;
    private int amount = 1;
    private int damage;
    private int customModelData;
    private boolean glow;
    private int priority;
    private boolean view;
    private boolean update;
    public List<Pair<String, ClickType>> clickCommands;
    public List<String> onUpdate;
    private List<String> viewRequirements;

    public static FlareStack wrap(ConfigurationSection section) {
        FlareStack stack = new FlareStack();
        for (String key : section.getKeys(false)) {
            switch (key.toLowerCase(Locale.ROOT)) {
                case "material":
                    stack.setMaterial(FlareMaterial.wrapFromString(section.getString(key)));
                    break;
                case "display_name":
                    stack.setDisplayName(section.getString(key));
                    break;
                case "lore":
                    stack.setLore(section.getStringList(key));
                    break;
                case "custom_model":
                case "custom_model_data":
                case "data":
                    stack.setCustomModelData(section.getInt(key));
                    break;
                case "damage":
                    stack.setDamage(section.getInt(key));
                    break;
                case "amount":
                    stack.setAmount(section.getInt(key));
                    break;
                case "glow":
                    stack.setGlow(section.getBoolean(key));
                    break;
                case "view":
                    stack.setView(section.getBoolean(key));
                    break;
                case "update":
                    stack.setUpdate(section.getBoolean(key));
                    break;
                case "view_requirements":
                case "view_requirement_list":
                    stack.setViewRequirements(section.getStringList(key));
                    break;
                case "on_update":
                    stack.setOnUpdate(section.getStringList(key));
                    break;
                case "click_commands":
                    Pattern pat = Pattern.compile("(?i)(\\[(LEFT|SHIFT_LEFT|RIGHT|SHIFT_RIGHT|MIDDLE|NUMBER_KEY|DOUBLE_CLICK|DROP|CONTROL_DROP|SWAP_OFFHAND)\\])?(.+)");
                    stack.clickCommands = new ArrayList<>();
                    for(String s : section.getStringList(key)) {
                        Matcher matcher = pat.matcher(s);
                        if(!matcher.matches())
                            continue;
                        if(matcher.group(2) != null)
                            stack.clickCommands.add(new Pair<>(matcher.group(3).trim(), ClickType.valueOf(matcher.group(2).toUpperCase(Locale.ROOT))));
                        else stack.clickCommands.add(new Pair<>(matcher.group(3).trim(), null));
                    }
                    break;
            }
        }
        return stack;
    }

    public FlareStack wrap(FlareStackFrame frame) {
        return frame.build();
    }

    // Input

    public void onClick(Player player, ClickType clickType, Replace... replace) {
        if(clickCommands == null)
            return;
        List<String> commands = new ArrayList<>();
        for(Pair<String, ClickType> actual : clickCommands) {
            if(actual.second != null) {
                if(actual.second.equals(clickType))
                    commands.add(Message.format(actual.first, player, replace));
            } else
                commands.add(Message.format(actual.first, player, replace));
        }
        FlareScript flareScript = new FlareScript();
        flareScript.processFull(commands, player);
    }

    public void onUpdate(Player player, Replace... replace) {
        if(onUpdate == null)
            return;
        new FlareScript().processFull(Message.format(onUpdate, player, replace), player);
    }

    public boolean onView(Player player, Replace... replace) {
        //TODO add view cache
        if(viewRequirements == null) return true;
        if(viewRequirements.isEmpty()) return true;
        return new FlareScript().processComponents(Message.format(viewRequirements, player, replace), player);
    }

    /**
     * @return true if the items was correctly gave to the player else false (The inventory might be full)!
     */
    public boolean giveItem(Player player, Replace... replace) {
        try {
            if(!player.getInventory().addItem(construct(player, replace)).isEmpty())
                return false;
        } catch (ItemBuilderConfigurationException e) {
            Logger.error("You tried to give a player an item with this error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public ItemStack construct(Player player, Replace... replace) throws ItemBuilderConfigurationException {
        if(material == null) throw new ItemBuilderConfigurationException("You need to add a material!");
        ItemStack itemStack = material.construct(player);
        ItemMeta im = itemStack.getItemMeta();
        if(im == null) return itemStack;
        itemStack.setAmount(amount);
        /* Display */
        if(displayName != null) im.setDisplayName(Message.format(displayName, player, replace));
        if (lore != null)
            im.setLore(Message.format(lore, player, replace));
        if (glow) {
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        im.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        im.hasItemFlag(ItemFlag.HIDE_DESTROYS);
        im.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);

        /* Model and Damage */
        if(material.isNormalItem()) {
            if (VersionControl.getVersion() > 13) {
                im.setCustomModelData(customModelData);
                itemStack.setItemMeta(im);
            }
            if(im instanceof Damageable) {
                Damageable imd = (Damageable) im;
                imd.setDamage(damage);
                itemStack.setItemMeta(imd);
            }
        } else itemStack.setItemMeta(im);

        /* finish */
        return itemStack;
    }

    @Override
    public String toString() {
        return "FlareStack{" +
                "material=" + material +
                ", displayName='" + displayName + '\'' +
                ", lore=" + lore +
                ", amount=" + amount +
                ", damage=" + damage +
                ", customModelData=" + customModelData +
                ", glow=" + glow +
                ", priority=" + priority +
                ", view=" + view +
                ", update=" + update +
                ", clickCommands=" + clickCommands +
                ", viewRequirements=" + viewRequirements +
                '}';
    }
}
