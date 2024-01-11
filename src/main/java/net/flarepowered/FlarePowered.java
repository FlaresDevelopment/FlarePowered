package net.flarepowered;

import lombok.Getter;
import net.flarepowered.core.TML.check.*;
import net.flarepowered.core.TML.components.ConsoleComponent;
import net.flarepowered.core.TML.components.ServerSwitchComponent;
import net.flarepowered.core.TML.components.economy.MobcoinsComponent;
import net.flarepowered.core.TML.components.economy.MoneyComponent;
import net.flarepowered.core.TML.components.economy.TokensComponent;
import net.flarepowered.core.TML.components.menu.AddItemComponent;
import net.flarepowered.core.TML.components.menu.ExitComponent;
import net.flarepowered.core.TML.components.menu.RemoveItemComponent;
import net.flarepowered.core.TML.components.menu.VariableComponent;
import net.flarepowered.core.TML.components.player.*;
import net.flarepowered.core.TML.objects.TMLArray;
import net.flarepowered.core.text.MessageEngine;
import net.flarepowered.core.text.bossbar.BossBarUtils;
import net.flarepowered.core.text.placeholders.DefaultPlaceholders;
import net.flarepowered.core.text.placeholders.Placeholder;
import net.flarepowered.neo.ui.MenuArchitect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum FlarePowered {
    LIB;
    private JavaPlugin plugin;
    private TMLArray TMLObject;
    private BossBarUtils bossBarUtils;
    private List<Placeholder> placeholders;
    private MessageEngine messageEngine;
    
    public void useLib(JavaPlugin plugin) {
        this.plugin = plugin;
        this.messageEngine = new MessageEngine();
        messageEngine.start();
        loadTMLObject();
        loadDefaults();
    }

    public void onDisable() {
        messageEngine.disable();
        if(bossBarUtils != null)
            bossBarUtils.bossbars.keySet().forEach(uuid -> {
                if(Bukkit.getPlayer(uuid) != null)
                    bossBarUtils.clearBossBarsFromPlayer(Bukkit.getPlayer(uuid));
            });
    }

    public void enableMenus() {
        MenuArchitect.MENU.onEnable();
    }

    public void enableBossBars() {
        this.bossBarUtils = new BossBarUtils();
        this.bossBarUtils.runner();
        plugin.getServer().getPluginManager().registerEvents(bossBarUtils, plugin);
    }

    private void loadDefaults() {
        this.placeholders = new ArrayList<>();
        addNewPlaceholder(new DefaultPlaceholders());
    }

    public void addNewPlaceholder (Placeholder placeholder) {
        placeholders.add(placeholder);
    }

    private void loadTMLObject() {
        this.TMLObject = new TMLArray();
        // TODO implement using reflection pls
        TMLObject.addComponent(new ConsoleComponent(), new ServerSwitchComponent(), new TitleComponent(), new SudoComponent(), new SoundComponent(),
                new PlayerComponent(), new MessageComponent(), new EffectComponent(), new BossbarComponent(), new ActionBarComponent(),
                new MobcoinsComponent(), new MoneyComponent(), new TokensComponent(), new BuyComponent());
        TMLObject.addComponent(new RemoveItemComponent(), new AddItemComponent(), new ExitComponent(), new VariableComponent());
        TMLObject.addRequirement(new ExpressionCheck(), new ItemCheck(), new JavaScriptCheck(), new MoneyCheck(), new PermissionCheck());
    }

}
