package net.flarepowered.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.flarepowered.core.menus.objects.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.UUID;

public class HeadUtils {

    public static ItemStack getHeadFromBase64(String value) {
        ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }

    public static String getBase64FromHead(ItemStack head) {
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        try {
            Field field = null;
            field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            GameProfile profile = (GameProfile) field.get(meta);
            Iterator<Property> textures = profile.getProperties().get("textures").iterator();
            if (textures.hasNext())
                return textures.next().getValue();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    return null;
    }

    public static ItemStack getHeadFromName(Player player) {
        ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(player.getPlayerProfile());
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack getHeadFromName(OfflinePlayer player) {
        ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(player.getPlayerProfile());
        head.setItemMeta(meta);
        return head;
    }

    /**
     * This will not WORK on 1.8-1.16 (ib)
     * @param value
     * @return
     */
    @Deprecated
    public static ItemStack getHeadFromValue(String value) {
        UUID id = UUID.nameUUIDFromBytes(value.getBytes());
        int less = (int) id.getLeastSignificantBits();
        int most = (int) id.getMostSignificantBits();
        return Bukkit.getUnsafe().modifyItemStack(
                XMaterial.PLAYER_HEAD.parseItem(),
                "{SkullOwner:{Id:[I;" + (less * most) + "," + (less >> 23) + "," + (most / less) + "," + (most * 8731) + "],Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }
}
