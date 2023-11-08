package net.flarepowered.core.text;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.data.yaml.YamlFile;
import net.flarepowered.core.text.bossbar.schematics.BossbarSchematic;
import net.flarepowered.core.text.other.FormatterUtils;
import net.flarepowered.core.text.other.Replace;
import net.flarepowered.other.Logger;
import net.flarepowered.utils.VersionControl;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.module.Configuration;
import java.util.List;
import java.util.stream.Collectors;

public class Message {

    /**
     * A thread safe message utility
     */

    private static final MessageEngine messageEngine = FlarePowered.LIB.getMessageEngine();

    public static void sendMessage(String s, Object player, Replace... replace) {
        if(player instanceof Player) {
            Player pl = (Player) player;
            pl.spigot().sendMessage(messageEngine.formatComponents(s, pl, replace));
            return;
        }
        if(player instanceof CommandSender) {
            CommandSender pl = (CommandSender) player;
            pl.sendMessage(messageEngine.formatNormal(s, pl, replace));
            return;
        }
    }

    public static void sendBasicMessage(String s, Object player, Replace... replace) {
        if(player instanceof Player) {
            Player pl = (Player) player;
            pl.sendMessage(messageEngine.formatNormal(s, pl, replace));
            return;
        }
        if(player instanceof CommandSender) {
            CommandSender pl = (CommandSender) player;
            pl.sendMessage(messageEngine.formatNormal(s, pl, replace));
            return;
        }
    }

    public static void sendLocalizedMessage(String s, Object player, Replace... replace) {
        if(player instanceof Player) {
            Player pl = (Player) player;
            pl.spigot().sendMessage(messageEngine.formatLocalizedMessage(s, pl, replace));
            return;
        }
        if(player instanceof CommandSender) {
            CommandSender pl = (CommandSender) player;
            pl.sendMessage(messageEngine.formatNormalLocalizedMessage(s, pl, replace));
            return;
        }
    }

    public static void sendImageToPlayer(final String URL, final boolean isWebURL, final MessageEngine.ImageCharacters imageCharacters, final Player player, String... content) {
        messageEngine.sendImageMessage(URL, isWebURL, 8, imageCharacters, player, content);
    }

    public static void sendImageToPlayer(final String URL, final boolean isWebURL, final int size, final MessageEngine.ImageCharacters imageCharacters, final Player player, String... content) {
        messageEngine.sendImageMessage(URL, isWebURL, size, imageCharacters, player, content);
    }

    public static void playTitle(final Player player, final String title, final String subtitle) {
        if(VersionControl.getVersion() >= 9)
            player.sendTitle(format(title, player), format(subtitle, player), 20, 100, 20);
    }

    public static void playTitle(final Player player, final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        if(VersionControl.getVersion() >= 9)
            player.sendTitle(format(title, player), format(subtitle, player), fadeIn, stay, fadeOut);
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(format(message, player)));
    }

    /* BossBars */

    public static void playBossBar(Player player, String id, BossbarSchematic bossbarSchematic) {
        if(FlarePowered.LIB.getBossBarUtils() == null) { Logger.error("Bossbars are not eanbled on this plugin!"); return; }
        FlarePowered.LIB.getBossBarUtils().addBossBarToPlayer(player, id, bossbarSchematic);
    }
    public static void removeBossBar(Player player, String id) {
        if(FlarePowered.LIB.getBossBarUtils() == null) { Logger.error("Bossbars are not eanbled on this plugin!"); return; }
        FlarePowered.LIB.getBossBarUtils().removeBossBarToPlayer(player, id);
    }
    public static void clearBossBar(Player player) {
        if(FlarePowered.LIB.getBossBarUtils() == null) { Logger.error("Bossbars are not eanbled on this plugin!"); return; }
        FlarePowered.LIB.getBossBarUtils().clearBossBarsFromPlayer(player);
    }

    /* Formats */

    public static String format(String s, Object player, Replace... replace) {
        return messageEngine.formatNormal(s, player, replace);
    }

    public static BaseComponent[] formatComponents(String s, Object player, Replace... replace) {
        return messageEngine.formatComponents(s, player, replace);
    }

    public static List<String> format(List<String> s, Object player, Replace... replace) {
        return s.stream().map(str -> messageEngine.formatNormal(str, player, replace)).collect(Collectors.toList());
    }

    public static String formatFromLocale(String path, Object player, Replace... replace) {
        return messageEngine.formatNormalLocalizedMessage(path, player, replace);
    }

    public static YamlFile getLocaleFile() {
        return messageEngine.getLocale();
    }

    public static FileConfiguration getLocale() {
        return messageEngine.getLocale().getConfig();
    }

    public static void reloadLocale() {
        messageEngine.reloadLocale();
    }

    public static void loadLocale(YamlFile locale) {
        messageEngine.loadLanguage(locale);
    }

}
