package net.flarepowered.utils;

import net.flarepowered.FlarePowered;
import net.flarepowered.other.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public class VersionControl {
    /**
     * @return the versions as (1.8 - 8; 1.18 - 18 etc)
     */
    public static int getVersion() {
        String version = Bukkit.getVersion();
        int index = version.lastIndexOf("MC:");
        if (index != -1) {
            version = version.substring(index + 4, version.length() - 1);
        } else if (version.endsWith("SNAPSHOT")) {
            // getBukkitVersion()
            index = version.indexOf('-');
            version = version.substring(0, index);
        }

        // 1.13.2, 1.14.4, etc...
        int lastDot = version.lastIndexOf('.');
        if (version.indexOf('.') != lastDot) version = version.substring(0, lastDot);

        return Integer.parseInt(version.substring(2));
    }

    public static void getLatestVersionFromSpigotMC(final Consumer<String> consumer, int resourceId) {
        Bukkit.getScheduler().runTaskAsynchronously(FlarePowered.LIB.getPlugin(), () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream(); Scanner scanner = new Scanner(is)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                Logger.error("We cant check the version using SpigotAPI");
            }
        });
    }
}