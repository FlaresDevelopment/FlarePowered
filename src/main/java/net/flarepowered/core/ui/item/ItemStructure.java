package net.flarepowered.core.ui.item;

import net.flarepowered.core.text.StringUtils;
import net.flarepowered.utils.VersionControl;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemStructure {

    public ItemStack item = null;
    public MaterialStructure material = new MaterialStructure();
    public String displayName;
    public List<String> lore;
    public int customData;
    public int damage;
    public int amount = 1;
    //public Color leatherArmorColor;
    public boolean glow;
    public boolean update;
    public List<Integer> slot = new ArrayList<>();

    public List<String> onClickCommands;
    public List<String> viewRequirementList = new ArrayList<>();
    public boolean view = true;
    public long viewTimeUpdate = -1;
    private Player player;
    public int priority = 99;

    public ItemStructure() {}

    public static ItemStructure item() {
        return new ItemStructure();
    }

    /**
     * The new auto getter with better performance and error handling
     * @throws ItemBuilderConfigurationException This will be thrown if we will find an error in config.
     */
    public ItemStructure createItemFormConfig(Configuration config, String prefix, String itemName) throws ItemBuilderConfigurationException {
        String location = prefix + "." + itemName + ".";
        if(!config.contains(prefix + "." + itemName))
            throw new ItemBuilderConfigurationException("The menu is trying to load " + itemName + ", but there is not item by this name. Skipping item");
        if(config.contains(location + "material")) {
            String mat = config.getString(location + "material");
            material = new MaterialStructure();
            if(mat.matches("(?i)\\[(head|username|player|base64|itemsadder)\\] (.+)")) {
                Pattern pat = Pattern.compile("(?i)\\[(head|username|player|base64|itemsadder)\\] (.+)");
                Matcher matcher1 = pat.matcher(mat);
                matcher1.find();
                switch (matcher1.group(1).toLowerCase(Locale.ROOT)) {
                    case "head":
                        material.setToBase64(matcher1.group(2));
                        break;
                    case "username":
                        material.setToPlayerName(StringUtils.formatMessage(matcher1.group(2), player));
                        break;
                    case "player":
                        material.setToPlayerName(player.getName());
                        break;
                    case "itemsadder":
                        material.setItemsAdderItem(matcher1.group(2));
                        break;
                }
            } else
                material.setMaterial(StringUtils.formatMessage(mat, player));
        } else throw new ItemBuilderConfigurationException("We found a problem with the item: " + itemName + ", you did not setup an MATERIAL, skipping item.");
        if(config.contains(location + "display_name"))
            displayName = config.getString(location + "display_name");
        if(config.contains(location + "lore"))
            lore = config.getStringList(location + "lore");
        if(config.contains(location + "data"))
            customData = config.getInt(location + "data");
        if(config.contains(location + "amount"))
            amount = config.getInt(location + "amount");
        if(config.contains(location + "damage"))
            damage = config.getInt(location + "damage");
        if(config.contains(location + "glow"))
            glow = config.getBoolean(location + "glow");
        if(config.contains(location + "update"))
            update = config.getBoolean(location + "update");
        if(config.contains(location + "click_commands"))
            onClickCommands = config.getStringList(location + "click_commands");
        if(config.contains(location + "view_requirement"))
            viewRequirementList.add(config.getString(location + "view_requirement"));
        if(config.contains(location + "view_requirement_list"))
            viewRequirementList.addAll(config.getStringList(location + "view_requirement_list"));
        // add leather color
        getSlots(config, prefix, itemName);
        return this;
    }

    private void getSlots(Configuration configuration, String prefix, String itemName) throws ItemBuilderConfigurationException {
        String location = prefix + "." + itemName + ".";
        if(configuration.contains(location + "slots")) {
            slot.addAll(configuration.getIntegerList(location + "slots"));
        } else if (configuration.contains(location + "slot")) {
            if(String.valueOf(configuration.get(location + "slot")).matches("(\\d+)-(\\d+)")) {
                String[] strings = String.valueOf(configuration.get(location + "slot")).split("-");
                for(int i = Integer.parseInt(strings[0]); i <= Integer.parseInt(strings[1]); i++) {
                    slot.add(i);
                }
            } else
                slot.add(configuration.getInt(location + "slot"));
        }
    }

    public ItemStructure setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ItemStructure setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public ItemStructure setSlots(List<Integer> slot) {
        this.slot = slot;
        return this;
    }

    public ItemStructure setSlots(int slot) {
        this.slot.add(slot);
        return this;
    }

    public ItemStructure setInformation(int customData, boolean update) {
        this.update = update;
        this.customData = customData;
        return this;
    }

    public ItemStructure setDisplay(String displayName, List<String> lore) {
        this.lore = lore; this.displayName = displayName;
        return this;
    }
    public ItemStructure setClickCommands(List<String> onClickCommands) {
        this.onClickCommands = onClickCommands;
        return this;
    }

    /**
     * This will check if the player can see the item.
     * @return true if the condition are meet, false if not.
     */
    public boolean isInView() {
//        if(viewTimeUpdate == -1) {
//            if(!viewRequirementList.isEmpty()) {
//                view = new FlareScript().processFull(viewRequirementList, player);
//                viewTimeUpdate = System.currentTimeMillis() + 2000;
//            }
//        } else if(viewTimeUpdate <= System.currentTimeMillis()) {
//            viewTimeUpdate = -1;
//        }
        return view;
    }

    public void onItemClick(Player player) {
//        if (onClickCommands != null) {
//            FlareScript flareScript = new FlareScript();
//            flareScript.processFull(onClickCommands, player);
//        }
    }

    public ItemStack construct() throws ItemBuilderConfigurationException {
        if(material == null)
            throw new ItemBuilderConfigurationException("The item that we tried to build is null, we are skipping this item.");
        if(item == null)
            item = material.construct();
        // Item build logic
        ItemMeta meta = item.getItemMeta();
        if(this.lore != null) {
            List<String> newLore = new ArrayList<>();
            for (String lore : this.lore)
                newLore.add(StringUtils.formatMessage(lore, player));
            meta.setLore(newLore);
        }
        if(this.displayName != null) meta.setDisplayName(StringUtils.formatMessage(displayName, player));
        item.setAmount(amount);
        // HIDE FLAGS
        meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        meta.hasItemFlag(ItemFlag.HIDE_DESTROYS);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
        meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
        // Other stuff
        if(glow)
            item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
        if(material.materialType != MaterialStructure.MaterialType.ITEMS_ADDER) {
            if (VersionControl.getVersion() >= 13)
                meta.setCustomModelData(customData);
        }
        item.setItemMeta(meta);
        return this.item;
    }

    /*@Deprecated
    public ItemStack build() {
        if(item == null)
            if(material.contains("[HEAD]"))
                item = HeadUtils.getHeadFromBase64(material.replace("[HEAD] ", ""));
            else if(material.contains("[PLAYER_HEAD]"))
                item = HeadUtils.getHeadFromName(player);
            else
                item = XMaterial.matchXMaterial(material).get().parseItem();
        List<String> l = new ArrayList<>();
        for (String s : this.lore)
            l.add(MessageHandler.chat(s).placeholderAPI(player).toStringColor());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageHandler.chat(displayName).placeholderAPI(player).toStringColor());
        meta.setLore(l);
        item.setAmount(amount);
        meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        meta.hasItemFlag(ItemFlag.HIDE_DESTROYS);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
        meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
        if(VersionCheckers.getVersion() >= 13)Objects.requireNonNull(item.getItemMeta()).setCustomModelData(customData);
        item.setItemMeta(meta);
        return this.item;
    }*/

}
