package net.flarepowered.core.text.bossbar;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.text.StringUtils;
import net.flarepowered.other.Logger;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class BossBarObject {

    public String id;
    public Player owner;
    public int screenTime;
    public int timePassed;
    public boolean update;
    public String title;
    public BarStyle barStyle;
    public BarColor barColor;
    public BarFlag barFlag;

    public BossBar bossBar;

    public void buildBossBar(Player player) {
        this.owner = player;
        if(id == null) {
            Logger.error("The bossbar is missing an ID! message=(" + title+ ")");
        }
        this.bossBar =  Bukkit.createBossBar(StringUtils.formatMessage(title, owner), barColor, barStyle);
    }

    public void update() {
        bossBar.setTitle(StringUtils.formatMessage(title, owner));
    }

    public void addToPlayer() {
        bossBar.addPlayer(owner);
    }
    public void removeFromPlayer() {
        bossBar.removePlayer(owner);
    }

//    public void newBossBarToPlayer(Player player, String s, BarColor barColor, BarStyle barStyle) {
//        if(bossBars.containsKey(player)) {
//            bossBars.get(player).removePlayer(player);
//        }
//        BossBar bossBar = Bukkit.createBossBar(s, barColor, barStyle);
//        bossBars.put(player, bossBar);
//        bossBars.get(player).addPlayer(player);
//    }
//    public void removeBossBarFromPlayer(Player player) {
//        if(bossBars.containsKey(player)) {
//            bossBars.get(player).removePlayer(player);
//        }
//    }

}
