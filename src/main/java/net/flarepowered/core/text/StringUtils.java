package net.flarepowered.core.text;

import me.clip.placeholderapi.PlaceholderAPI;
import net.flarepowered.FlarePowered;
import net.flarepowered.core.text.cache.ImageCache;
import net.flarepowered.core.text.images.ImageMessage;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.core.text.placeholders.PlaceholderParser;
import net.flarepowered.utils.VersionControl;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

public class StringUtils {

    //TODO add cache clean up!
    public static HashMap<ImageCache, ImageMessage> imageCache = new HashMap<>();
    public static HashMap<String, FileConfiguration> lang = new HashMap<>();
    public static String default_lang = "en";

    public static void sendImageMessageToPlayer(final String URL, final boolean isWebURL, final ImageCharacters imageCharacters, final Player player, String... content) {
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
                    imageCache.put(cache, new ImageMessage(imageToSend, 8, imageCharacters.getChar(), player).appendText(content));
                    imageCache.get(cache).sendToPlayer(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(FlarePowered.LIB.getPlugin());
    }

    public static void sendTitleMessageToPlayer(final Player player, final String title, final String subtitle, int fadeIn, int stay, int fadeOut) {
        if(VersionControl.getVersion() >= 13)
            player.sendTitle(formatMessage(title, player), formatMessage(subtitle, player), fadeIn, stay, fadeOut);
    }

    public static void sendActionBarToPlayer(final String message, final Player player) {
        if(VersionControl.getVersion() >= 13) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formatMessage(message, player)));
        } else {
            // TODO add 1.8 - 1.13 support
        }
    }


    public static String formatMessage(final String message, final Player player) {
        return format(message, player, FormatFlags.WITH_PLACEHOLDERS, FormatFlags.WITH_COLORS);
    }

    public static String formatMessageWithColors(final String message, final Player player) {
        return format(message, player, FormatFlags.WITH_COLORS);
    }

    public static String formatMessage(String message, final Player player, Replace... replaces) {
        for (Replace replace : replaces)
            message = message.replace(replace.from, replace.to);
        return format(message, player, FormatFlags.WITH_PLACEHOLDERS, FormatFlags.WITH_COLORS);
    }

    public static String formatMessageFromLocale(String message, final Player player) {
        String selected = default_lang;
        if(player != null)
            if(lang.containsKey(player.getLocale().toLowerCase().split("_")[1]))
                selected = player.getLocale().toLowerCase().split("_")[1];
        message = lang.get(selected).getString(message);
        assert message != null;
        message = message.replace("%pl_prefix%", Objects.requireNonNull(lang.get(selected).getString("prefix")));
        return format(message, player, FormatFlags.WITH_PLACEHOLDERS, FormatFlags.WITH_COLORS);
    }

    public static String formatMessageFromLocale(String message, final Player player, Replace... replaces) {
        String selected = default_lang;
        if(player != null)
            if(lang.containsKey(player.getLocale().toLowerCase().split("_")[1]))
                selected = player.getLocale().toLowerCase().split("_")[1];
        message = lang.get(selected).getString(message);
        assert message != null;
        message = message.replace("%pl_prefix%", Objects.requireNonNull(lang.get(selected).getString("prefix")));
        for (Replace replace : replaces)
            message = message.replace(replace.from, replace.to);
        return format(message, player, FormatFlags.WITH_PLACEHOLDERS, FormatFlags.WITH_COLORS);
    }

    public static FileConfiguration getLocalConfig(final Player player) {
        if(player == null)
            return lang.get(default_lang);
        else if(lang.containsKey(player.getLocale().toLowerCase()))
            return lang.get(player.getLocale().toLowerCase());
        else
            return lang.get(default_lang);
    }

    public static void reloadLang(final Player player) {
        lang.forEach((s, yamlFile) -> System.out.println("add reload logic"));
    }

    public static void loadLang(final Path path) {
        try (Stream<Path> paths = Files.walk(path)) {
            paths.filter(Files::isRegularFile)
                 .forEach(a -> lang.put(String.valueOf(a.getFileName()).replace(".yml", ""),
                         YamlConfiguration.loadConfiguration(new File(a.toString()))
                 ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will make you a MiniMessage component
     * @param message the message
     * @param player The player for placeholders
     * @return the component
     */
    public static Component formatToComponent(final String message, final Player player) {
        return formatToComponent(message, FormatFlags.WITH_PLACEHOLDERS, FormatFlags.WITH_COLORS);
    }

    /**
     * This will format the string with the format flags.
     * @param s the string.
     * @param flags all the flags for formatting
     * @return the string formatted
     */
    private static String format(String s, Player player, FormatFlags... flags) {
        // Placeholders
        if(Arrays.asList(flags).contains(FormatFlags.WITH_PLACEHOLDERS)) s = PlaceholderParser.parse(s, player);
        if(FlarePowered.LIB.getPlugin().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            s = PlaceholderAPI.setPlaceholders(player, s);
        // Colors (IridiumAPI)
        s = ColorUtils.process(s);
        // Colors (MiniMessage)
        // TODO implement MiniMessage colors support.
        return s;
    }

    /**
     * This will format the string with the format flags.
     * @param s the string.
     * @param flags all the flags for formatting
     * @return the string formatted
     */
    private static Component formatToComponent(String s, FormatFlags... flags) {
        return Component.text(s);
    }

    enum FormatFlags {
        WITH_PLACEHOLDERS,
        WITH_COLORS
    }

    public enum ImageCharacters {
        BLOCK('█'),
        DARK_SHADE('▓'),
        MEDIUM_SHADE('▒'),
        LIGHT_SHADE('░');
        private char c;

        ImageCharacters(char c) {
            this.c = c;
        }

        public char getChar() {
            return c;
        }
    }

}
