package net.flarepowered.other;

import net.flarepowered.FlarePowered;

import java.util.logging.Level;

public class Debug {

    static boolean isEnabled = false;

    public static void enableDebug() {
        isEnabled = true;
    }
    public static void disableDebug() {
        isEnabled = false;
    }

    public static void info(String s) {
        if(isEnabled)
            FlarePowered.LIB.getPlugin().getLogger().log(Level.INFO, s);
    }

    public static void warn(String s) {
        if(isEnabled)
            FlarePowered.LIB.getPlugin().getLogger().log(Level.WARNING, s);
    }
    public static void error(String s) {
        if(isEnabled)
            FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, s);
    }
}
