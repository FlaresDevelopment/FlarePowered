package net.flarepowered.core.TML.objects;

import net.flarepowered.core.TML.check.Requirement;
import net.flarepowered.core.TML.components.Component;
import net.flarepowered.other.Logger;
import net.flarepowered.other.exceptions.CheckException;
import net.flarepowered.other.exceptions.ComponentException;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TMLArray {

    List<Component> components = new ArrayList<>();
    List<Requirement> requirements = new ArrayList<>();

    public TMLState code(String s, Player player) {
        boolean check = true;
        if(s.contains("{if}")) {
            Matcher matcher = Pattern.compile("\\{if}(.*?)\\{do}(.*?)\\{\\/do}(\\{else}(.*?)\\{\\/else})*\\{end}").matcher(s);
            while(matcher.find()) {
                if (matcher.group(1) != null) {
                    String[] strings = matcher.group(1).split("\\{\\|}");
                    for (String req : strings) {
                        if (checkRequirement(req, player) == TMLState.CHECK_FALL) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        if (matcher.group(2) != null) {
                            String[] commands = matcher.group(2).split("\\{\\|}");
                            for (String cmd : commands) {
                                runCommands(cmd, player);
                            }
                        } else {
                            Logger.warn("There is no command in this sequence.");
                        }
                    } else {
                        if (matcher.group(4) != null) {
                            String[] commands = matcher.group(4).split("\\{\\|}");
                            for (String cmd : commands) {
                                runCommands(cmd, player);
                            }
                        } else {
                            Logger.warn("There is no command in this sequence.");
                        }
                    }
                }
            }
        } else {
            TMLState tmlState = process(s, player);
            if(tmlState == TMLState.FORCED_QUIT)
                return TMLState.FORCED_QUIT;
            if(tmlState == TMLState.COMPLETED || tmlState == TMLState.CHECK_SUCCESS)
                return TMLState.COMPLETED;
            if(tmlState == TMLState.CHECK_FALL)
                return TMLState.CHECK_FALL;
        }
        return check ? TMLState.COMPLETED : TMLState.CHECK_FALL;
    }

    private TMLState checkRequirement(String s, Player player) {
        for(Requirement req : requirements) {
            try {
                if(req.run(s, player) == TMLState.CHECK_SUCCESS)
                    return TMLState.CHECK_SUCCESS;
            } catch (CheckException e) {
                Logger.error("An error has spawned, this is the message I got from: " + e.getMessage());
            }
        }
        return TMLState.CHECK_FALL;
    }

    private TMLState runCommands(String s, Player player) {
        for(Component com : components) {
            try {
                if(com.run(s, player) == TMLState.COMPLETED)
                    return TMLState.COMPLETED;
            } catch (ComponentException e) {
                Logger.error("An error has spawned, this is the message I got from: " + e.getMessage());
            }
        }
        return TMLState.CHECK_FALL;
    }

    private TMLState process(String s, Player player) {
        if(s.toLowerCase(Locale.ROOT).contains("check") || s.toLowerCase(Locale.ROOT).contains("require")) {
            for(Requirement req : requirements) {
                try {
                    return req.run(s, player);
                } catch (CheckException e) {
                    Logger.error("An error has spawned, this is the message I got from: " + e.getMessage());
                }
            }
        } else if(s.matches("(?i)\\[.*?] (.*)")) {
            for(Component com : components) {
                try {
                    TMLState state = com.run(s, player);
                    if(state == TMLState.FORCED_QUIT || state == TMLState.COMPLETED)
                        return state;
                } catch (ComponentException e) {
                    Logger.error("An error has spawned, this is the message I got from: " + e.getMessage());
                }
            }
        }
        return TMLState.UNKNOWN;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void addComponent(Component... com) {
        this.components.addAll(Arrays.asList(com));
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(Requirement... req) {
        this.requirements.addAll(Arrays.asList(req));
    }
}
