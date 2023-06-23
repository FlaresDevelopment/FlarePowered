package net.flarepowered.utils;

import lombok.Getter;
import net.flarepowered.FlarePowered;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

@Getter
public enum DependencyManager {
    GET;
    private Economy vaultEconomy;

    public boolean isPluginLoaded(Dependency dependency) {
        return FlarePowered.LIB.getPlugin().getServer().getPluginManager().getPlugin(dependency.toString()) != null;
    }

    public void loadDependencies() {
        // Vault API
        if(isPluginLoaded(Dependency.Vault))
            setupEconomy();
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
        WorldGuard

    }

}
