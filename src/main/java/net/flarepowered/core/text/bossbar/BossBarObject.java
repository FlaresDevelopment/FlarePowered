package net.flarepowered.core.text.bossbar;

import net.flarepowered.core.text.Message;
import net.flarepowered.core.text.other.StringUtils;
import net.flarepowered.other.Logger;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarObject {

    public String id;
    public boolean enableDecreaseAnimation;
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
        this.bossBar = Bukkit.createBossBar(Message.format(title, owner), barColor, barStyle);
        if (enableDecreaseAnimation)
            this.bossBar.setProgress(0);
    }

    public void update() {
        bossBar.setTitle(Message.format(title, owner));
    }

    public void addToPlayer() {
        bossBar.addPlayer(owner);
    }
    public void removeFromPlayer() {
        bossBar.removePlayer(owner);
    }

}
