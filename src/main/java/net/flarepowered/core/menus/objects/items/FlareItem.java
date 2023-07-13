package net.flarepowered.core.menus.objects.items;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.TML.FlareScript;
import net.flarepowered.core.menus.other.ItemType;
import net.flarepowered.core.text.StringUtils;
import net.flarepowered.other.Logger;
import net.flarepowered.other.exceptions.ItemBuilderConfigurationException;
import net.flarepowered.utils.VersionControl;
import net.flarepowered.utils.objects.Pair;
import org.bukkit.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlareItem {
    /* Menu parameters */
    public List<Byte> slot = new ArrayList<>();
    public int page;
    public int priority;
    public boolean canBeViewed = true;
    public ItemType itemType = ItemType.NORMAL;
    public boolean update;
    public boolean reactive;
    public Pair<String, String> reactiveConfig;
    public List<String> clickCommands;
    public List<String> onItemPlaceCommands;
    public List<String> onUpdateCommands;

    /* Item meta */
    public String material;
    public int damage;
    public int amount = 1;
    public String displayName;
    public List<String> lore;
    public List<String> viewRequirementList = new ArrayList<>();
    public int customModelData;
    /* Other */
    public Color horseColor;
    public boolean glow;

    public FlareItem getObject() throws ItemBuilderConfigurationException {
        if (!reactive)
            return this;
        getAutomaticallyFromConfig();
        return this;
    }

    public FlareItem setConfig(String config, String path) {
        reactiveConfig = new Pair<>(path, config);
        reactive = true;
        return this;
    }

    public void onClickCommands(Player player) {
        if(clickCommands == null)
            return;
        List<String> commands = clickCommands.stream().map(clickCommand -> StringUtils.formatMessage(clickCommand, player)).collect(Collectors.toCollection(ArrayList::new));
        FlareScript flareScript = new FlareScript();
        flareScript.processFull(commands, player);
    }

    private void getAutomaticallyFromConfig() throws ItemBuilderConfigurationException {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FlarePowered.LIB.getPlugin().getDataFolder(), reactiveConfig.second));
        String path = reactiveConfig.first;
        if (config.contains(path + ".material"))
            this.material = config.getString(path + ".material");
        else throw new ItemBuilderConfigurationException("The item from " + path + " has no material name.");
        if (config.contains(path + ".display_name"))
            this.displayName = config.getString(path + ".display_name");
        else throw new ItemBuilderConfigurationException("The item from " + path + " has no display name.");
        if (config.contains(path + ".update"))
            this.update = config.getBoolean(path + ".update");
        if (config.contains(path + ".click_commands"))
            this.clickCommands = config.getStringList(path + ".click_commands");
        try {
            if (config.contains(path + ".slot")) {
                String value = config.getString(path + ".slot");
                if(value.contains("-"))
                    setSlotByRange(value);
                else
                    setSlot(Byte.parseByte(value));
            }
            if (config.contains(path + ".slots"))
                setSlots(config.getByteList(path + ".slots"));
            if(slot.isEmpty()) {
                Logger.warn("The item from " + path + " has no slot number, the default 0 will be used!.");
                setSlot((byte) 0);
            }
        } catch (Exception e) {
            Logger.error("The item from " + path + " has an invalid slots numbers, error code: " + e.getMessage());
        }
        if(config.contains(path + ".lore"))
            lore = config.getStringList(path + ".lore");
        if(config.contains(path + ".data"))
            customModelData = config.getInt(path + ".data");
        if(config.contains(path + ".amount"))
            amount = config.getInt(path + ".amount");
        if(config.contains(path + ".damage"))
            damage = config.getInt(path + ".damage");
        if(config.contains(path + ".glow"))
            glow = config.getBoolean(path + ".glow");
        if(config.contains(path + ".view_requirement"))
            viewRequirementList.add(config.getString(path + ".view_requirement"));
        if(config.contains(path + ".view_requirement_list"))
            viewRequirementList.addAll(config.getStringList(path + ".view_requirement_list"));
        if(config.contains(path + ".view"))
            canBeViewed = config.getBoolean(path + ".view");
    }

    public void setSlotByRange(String range) {
        String[] values = range.split("-");
        try {
            for(byte b = Byte.parseByte(values[0]); b <= Byte.parseByte(values[1]); b++)
                slot.add(b);
        } catch (Exception e) {
            Logger.error("You tried to put " + range + " as a slot range but it failed.");
        }
    }
    public void setSlots(List<Byte> list) {
        slot.addAll(list);
    }
    public void setSlot(Byte s) {
        slot.add(s);
    }

    public boolean canBeViewed (Player player) {
        if(viewRequirementList.isEmpty())
            return canBeViewed;
        FlareScript fl = new FlareScript();
        canBeViewed = fl.processFull((List<String>) viewRequirementList.stream()
                .map(a -> StringUtils.formatMessage(a, player))
                .collect(Collectors.toCollection(ArrayList::new)), player);
        return canBeViewed;
    }

    public ItemStack construct(Player player) throws ItemBuilderConfigurationException {
        ItemStack itemStack = MaterialStructure.getFromString(material, player).construct();
        ItemMeta im = itemStack.getItemMeta();
        itemStack.setAmount(amount);
        im.setDisplayName(StringUtils.formatMessage(displayName, player));
        if (lore != null)
            im.setLore(lore.stream()
                    .map(lore -> StringUtils.formatMessage(lore, player))
                    .collect(Collectors.toCollection(ArrayList::new)));
        if (VersionControl.getVersion() > 13)
            im.setCustomModelData(customModelData);
        if (glow) {
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        im.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        im.hasItemFlag(ItemFlag.HIDE_DESTROYS);
        im.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(im);
        return itemStack;
    }

    /* GETTERS */

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public List<String> getClickCommands() {
        return clickCommands;
    }

    public void setClickCommands(List<String> clickCommands) {
        this.clickCommands = clickCommands;
    }

    public List<String> getOnItemPlaceCommands() {
        return onItemPlaceCommands;
    }

    public void setOnItemPlaceCommands(List<String> onItemPlaceCommands) {
        this.onItemPlaceCommands = onItemPlaceCommands;
    }

    public List<String> getOnUpdateCommands() {
        return onUpdateCommands;
    }

    public void setOnUpdateCommands(List<String> onUpdateCommands) {
        this.onUpdateCommands = onUpdateCommands;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<String> getViewRequirementList() {
        return viewRequirementList;
    }

    public void setViewRequirementList(List<String> viewRequirementList) {
        this.viewRequirementList = viewRequirementList;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public Color getHorseColor() {
        return horseColor;
    }

    public void setHorseColor(Color horseColor) {
        this.horseColor = horseColor;
    }

    public boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }
}
