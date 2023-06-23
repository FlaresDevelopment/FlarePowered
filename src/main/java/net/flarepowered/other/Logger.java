package net.flarepowered.other;

import net.flarepowered.FlarePowered;

import java.util.logging.Level;

public class Logger {

    public static void info(String s) {
        FlarePowered.LIB.getPlugin().getLogger().log(Level.INFO, s);
    }

    public static void warn(String s) {
        FlarePowered.LIB.getPlugin().getLogger().log(Level.WARNING, s);
    }
    public static void error(String s) {
        FlarePowered.LIB.getPlugin().getLogger().log(Level.SEVERE, s);
    }
}
