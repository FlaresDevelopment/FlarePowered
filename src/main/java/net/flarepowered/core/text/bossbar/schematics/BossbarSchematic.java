package net.flarepowered.core.text.bossbar.schematics;

import net.flarepowered.core.text.bossbar.BossBarObject;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class BossbarSchematic {

    public BarStyle barStyle;
    public BarColor barColor;
    public BarFlag barFlag;
    public String title;
    public int screenTime = -1;
    public int progress;
    public boolean enableDecreaseAnimation;
    public int animate = -1;
    public boolean update = false;

    public BossbarSchematic() {
    }

    /**
     * Just a simple bar with no animations and infinite screen time.
     * @param barStyle BarStyle
     * @param barColor BarColor
     * @param title any string
     * @param update if the title will be updated. This will not have any effects on the animations!
     */
    public BossbarSchematic(BarStyle barStyle, BarColor barColor, String title, boolean update) {
        this.barStyle = barStyle;
        this.barColor = barColor;
        this.title = title;
        this.update = update;
    }

    /**
     * Just a simple bar with no animations with a predefined screen time.
     * @param barStyle BarStyle.
     * @param barColor BarColor.
     * @param title any string.
     * @param screenTime in seconds.
     * @param update if the title will be updated. This will not have any effects on the animations!
     */
    public BossbarSchematic(BarStyle barStyle, BarColor barColor, String title, int screenTime, boolean update) {
        this.barStyle = barStyle;
        this.barColor = barColor;
        this.title = title;
        this.screenTime = screenTime;
        this.update = update;
    }

    /**
     * Just a simple bar with no animations with a predefined screen time.
     * @param barStyle BarStyle.
     * @param barColor BarColor.
     * @param title any string.
     * @param screenTime in seconds.
     * @param enableDecreaseAnimation if you want to enable a cool countdown animation
     */
    public BossbarSchematic(BarStyle barStyle, BarColor barColor, String title, int screenTime, boolean update, boolean enableDecreaseAnimation) {
        this.barStyle = barStyle;
        this.barColor = barColor;
        this.title = title;
        this.screenTime = screenTime;
        this.update = update;
        this.enableDecreaseAnimation = enableDecreaseAnimation;
    }

    public BossBarObject toBossBarObject(Player player) {
        BossBarObject template = new BossBarObject();
        template.update = update;
        template.barColor = barColor;
        template.screenTime = screenTime;
        template.title = title;
        template.barStyle = barStyle;
        template.enableDecreaseAnimation = this.enableDecreaseAnimation;
        return template;
    }

}
