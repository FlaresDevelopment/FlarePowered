package net.flarepowered.core.text;

import me.clip.placeholderapi.PlaceholderAPI;
import net.flarepowered.FlarePowered;
import net.flarepowered.core.data.yaml.YamlFile;
import net.flarepowered.core.text.cache.ImageCache;
import net.flarepowered.core.text.images.ImageMessage;
import net.flarepowered.core.text.other.ColorUtils;
import net.flarepowered.core.text.other.FormatterUtils;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.core.text.placeholders.PlaceholderParser;
import net.flarepowered.other.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class MessageEngine {

    public void start() {

    }

    public void disable() {

    }

    public void reload() {

    }

    public void loadLanguage(YamlFile locale) {
        this.locale = locale;
    }

    public void reloadLocale() {
        this.locale.reloadConfig();
    }

    private YamlFile locale;

    public YamlFile getLocale() {
        return locale;
    }

    public String formatNormalLocalizedMessage (String path, Object player, Replace... replaces) {
        if(locale == null) { Logger.error("The locale is null!"); return ""; }
        if(!locale.getConfig().contains(path))
            return "Message was not found (" + path + ")";
        if(!locale.getConfig().contains("prefix"))
            return formatNormal(locale.getConfig().getString(path), player, replaces);
        return formatNormal(locale.getConfig().getString(path).replace("%pl_prefix%", locale.getConfig().getString("prefix")), player, replaces);
    }

    public BaseComponent[] formatLocalizedMessage (String path, Object player, Replace... replaces) {
        if(locale == null) { Logger.error("The locale is null!"); return new BaseComponent[0]; }
        if(!locale.getConfig().contains(path))
            return TextComponent.fromLegacyText("&cMessage was not found (" + path + ")");
        if(!locale.getConfig().contains("prefix"))
            return formatComponents(locale.getConfig().getString(path), player, replaces);
        return formatComponents(locale.getConfig().getString(path).replace("%pl_prefix%", locale.getConfig().getString("prefix")), player, replaces);
    }

    public String formatNormal(String s, Object player, Replace... replaces) {
        // Placeholders
        for (Replace replace : replaces)
            s = s.replace(replace.from, replace.to);
        Player player1 = null;
        if(player instanceof Player)
            player1 = (Player) player;
        s = PlaceholderParser.parse(s, player1);
        if(FlarePowered.LIB.getPlugin().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            s = PlaceholderAPI.setPlaceholders(player1, s);
        // Colors (IridiumAPI)
        s = ColorUtils.process(s);
        return s;
    }

    public BaseComponent[] formatComponents(String s, Object player, Replace... replaces) {
        return FormatterUtils.process(formatNormal(s, player, replaces), player instanceof Player ? (Player) player : null);
    }

    public static HashMap<ImageCache, ImageMessage> imageCache = new HashMap<>();

    public void sendImageMessage(final String URL, final boolean isWebURL, final int size, final MessageEngine.ImageCharacters imageCharacters, final Player player, String... content) {
        ImageCache cache = new ImageCache(URL, content);
        if(imageCache.containsKey(cache)) {
            imageCache.get(cache).sendToPlayer(player);
            return;
        }
        new BukkitRunnable() {
            @Override public void run() {
                try {
                    BufferedImage imageToSend;
                    if (isWebURL)
                        imageToSend = ImageIO.read(new URL(URL));
                    else
                        imageToSend =  ImageIO.read(new File(URL));
                    imageCache.put(cache, new ImageMessage(imageToSend, size, imageCharacters.getChar(), player).appendText(content));
                    imageCache.get(cache).sendToPlayer(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(FlarePowered.LIB.getPlugin());
    }

    public enum ImageCharacters {
        BLOCK('█'),
        DARK_SHADE('▓'),
        MEDIUM_SHADE('▒'),
        LIGHT_SHADE('░');
        private final char c;

        ImageCharacters(char c) {
            this.c = c;
        }

        public char getChar() {
            return c;
        }
    }

}
