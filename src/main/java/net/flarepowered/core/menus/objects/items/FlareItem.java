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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public List<Pair<String, ClickType>> clickCommands;
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

    public void onClickCommands(Player player, ClickType type) {
        if(clickCommands == null)
            return;
        List<String> commands = new ArrayList<>();
        for(Pair<String, ClickType> actual : clickCommands) {
            if(actual.second != null) {
                if(actual.second.equals(type))
                    commands.add(StringUtils.formatMessage(actual.first, player));
            } else
                commands.add(StringUtils.formatMessage(actual.first, player));
        }
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
        Pattern pat = Pattern.compile("(?i)(\\[(LEFT|SHIFT_LEFT|RIGHT|SHIFT_RIGHT|MIDDLE|NUMBER_KEY|DOUBLE_CLICK|DROP|CONTROL_DROP|SWAP_OFFHAND)\\])?(.+)");
        if (config.contains(path + ".click_commands")) {
            for(String s : config.getStringList(path + ".click_commands")) {
                Matcher matcher = pat.matcher(s);
                if(!matcher.matches())
                    continue;
                if(matcher.group(2) != null)
                    clickCommands.add(new Pair<>(matcher.group(3).trim(), ClickType.valueOf(matcher.group(2).toUpperCase(Locale.ROOT))));
                else clickCommands.add(new Pair<>(matcher.group(3).trim(), null));
            }
        }
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
    public FlareItem setSlots(List<Byte> list) {
        slot.addAll(list);
        return this;
    }
    public FlareItem setSlot(Byte s) {
        slot.add(s);
        return this;
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
        if(!material.contains("itemsadder")) {
            if (VersionControl.getVersion() > 13)
                im.setCustomModelData(customModelData);
        }
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

    public FlareItem setPage(int page) {
        this.page = page;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public FlareItem setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public boolean isCanBeViewed() {
        return canBeViewed;
    }

    public FlareItem setCanBeViewed(boolean canBeViewed) {
        this.canBeViewed = canBeViewed;
        return this;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public FlareItem setItemType(ItemType itemType) {
        this.itemType = itemType;
        return this;
    }

    public boolean isUpdate() {
        return update;
    }

    public FlareItem setUpdate(boolean update) {
        this.update = update;
        return this;
    }

    public boolean isReactive() {
        return reactive;
    }

    public FlareItem setReactive(boolean reactive) {
        this.reactive = reactive;
        return this;
    }

    public Pair<String, String> getReactiveConfig() {
        return reactiveConfig;
    }

    public FlareItem setReactiveConfig(Pair<String, String> reactiveConfig) {
        this.reactiveConfig = reactiveConfig;
        return this;
    }

    public List<String> getOnItemPlaceCommands() {
        return onItemPlaceCommands;
    }

    public FlareItem setOnItemPlaceCommands(List<String> onItemPlaceCommands) {
        this.onItemPlaceCommands = onItemPlaceCommands;
        return this;
    }

    public List<String> getOnUpdateCommands() {
        return onUpdateCommands;
    }

    public FlareItem setOnUpdateCommands(List<String> onUpdateCommands) {
        this.onUpdateCommands = onUpdateCommands;
        return this;
    }

    public String getMaterial() {
        return material;
    }

    public FlareItem setMaterial(String material) {
        this.material = material;
        return this;
    }

    public int getDamage() {
        return damage;
    }

    public FlareItem setDamage(int damage) {
        this.damage = damage;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public FlareItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public FlareItem setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public FlareItem setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public List<String> getViewRequirementList() {
        return viewRequirementList;
    }

    public FlareItem setViewRequirementList(List<String> viewRequirementList) {
        this.viewRequirementList = viewRequirementList;
        return this;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public FlareItem setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public Color getHorseColor() {
        return horseColor;
    }

    public FlareItem setHorseColor(Color horseColor) {
        this.horseColor = horseColor;
        return this;
    }

    public boolean isGlow() {
        return glow;
    }

    public FlareItem setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }
}
