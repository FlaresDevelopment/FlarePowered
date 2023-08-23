package net.flarepowered.utils;

import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import lombok.Getter;
import net.flarepowered.FlarePowered;
import net.flarepowered.other.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

@Getter
public enum DependencyManager {
    GET;
    private Economy vaultEconomy;
    private Plugin executableItems;

    public boolean isPluginLoaded(Dependency dependency) {
        return FlarePowered.LIB.getPlugin().getServer().getPluginManager().getPlugin(dependency.toString()) != null;
    }

    public void loadDependencies() {
        // Vault API
        if(isPluginLoaded(Dependency.Vault))
            setupEconomy();
        if(isPluginLoaded(Dependency.ExecutableItems)) {
            this.executableItems = Bukkit.getPluginManager().getPlugin("ExecutableItems");
            if(executableItems == null)
                Logger.warn("You cloud not load ExecutableItems!");
            executableItems.isEnabled();
        }
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = FlarePowered.LIB.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        vaultEconomy = rsp.getProvider();
    }

    public enum Dependency {
        PlaceholderAPI,
        Vault,
        ItemsAdder,
        FlareMobcoins,
        FlareTokens,
        FlarePanels,
        CMI,
        WorldEdit,
        WorldGuard,
        ExecutableItems,

    }

}
