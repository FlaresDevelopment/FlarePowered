package net.flarepowered.core.TML;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.TML.objects.CodeSequence;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * The newer TMLanguage, TMPL alternative and CBA.
 */
public class FlareScript {
    public boolean processFull(List<String> s, Player player) {
        boolean state = true;
        for(String str : CodeSequence.runAndCompile(s)) {
            switch (FlarePowered.LIB.getTMLObject().code(str, player)) {
                case FORCED_QUIT:
                    return false;
                case CHECK_FALL:
                    state = false;
            }
        }
        return state;
    }

    public boolean processFull(String s, Player player) {
        boolean state = true;
        for(String str : CodeSequence.runAndCompile(Collections.singletonList(s))) {
            switch (FlarePowered.LIB.getTMLObject().code(str, player)) {
                case FORCED_QUIT:
                    return false;
                case CHECK_FALL:
                    state = false;
            }
        }
        return state;
    }

    public boolean processComponents(List<String> s, Player player) {
        boolean state = true;
        for(String str : s) {
            switch (FlarePowered.LIB.getTMLObject().code(str, player)) {
                case FORCED_QUIT:
                    return false;
                case CHECK_FALL:
                    state = false;
            }
        }
        return state;
    }

    public boolean processComponents(String s, Player player) {
        boolean state = true;
        switch (FlarePowered.LIB.getTMLObject().code(s, player)) {
            case FORCED_QUIT:
                return false;
            case CHECK_FALL:
                state = false;
        }
        return state;
    }

}
