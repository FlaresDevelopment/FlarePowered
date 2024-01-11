package net.flarepowered.core.TML.components.menu;

import net.flarepowered.core.TML.components.Component;
import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.neo.ui.MenuArchitect;
import net.flarepowered.neo.ui.items.FlareMaterial;
import net.flarepowered.neo.ui.items.FlareStack;
import net.flarepowered.other.exceptions.ComponentException;
import net.flarepowered.utils.objects.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddItemComponent implements Component {

    /**
     * [remove_item] [slot] (amount)
     * [remove_item] [slot]
     */
    Pattern pattern = Pattern.compile("(?i)\\[add_item] (.*)");

    @Override
    public TMLState run(String string, Player player) throws ComponentException {
        Matcher main = pattern.matcher(string);
        if(main.find()) {
            if(main.group(1) == null) throw new ComponentException("This is not right!");
            Matcher matcher = Pattern.compile("([^ -\"]*)[-=][\"|']([^-=\"]*)[\"|']").matcher(main.group(1));
            FlareStack stack = new FlareStack();
            int slot = 0;
            while(matcher.find()) {
                System.out.println(matcher.group(1).toLowerCase(Locale.ROOT).trim() + " = " + matcher.group(2));
                switch (matcher.group(1).toLowerCase(Locale.ROOT).trim()) {
                    case "material":
                        stack.setMaterial(FlareMaterial.wrapFromString(matcher.group(2)));
                        break;
                    case "display_name":
                        stack.setDisplayName(matcher.group(2));
                        break;
                    case "lore":
                        stack.setLore(Arrays.asList(matcher.group(2).split("\\n")));
                        break;
                    case "custom_model":
                    case "custom_model_data":
                    case "data":
                        stack.setCustomModelData(Integer.parseInt(matcher.group(2)));
                        break;
                    case "damage":
                        stack.setDamage(Integer.parseInt(matcher.group(2)));
                        break;
                    case "amount":
                        stack.setAmount(Integer.parseInt(matcher.group(2)));
                        break;
                    case "glow":
                        stack.setGlow(Boolean.parseBoolean(matcher.group(2)));
                        break;
                    case "view":
                        stack.setView(Boolean.parseBoolean(matcher.group(2)));
                        break;
                    case "update":
                        stack.setUpdate(Boolean.parseBoolean(matcher.group(2)));
                        break;
                    case "view_requirement_list":
                        stack.setViewRequirements(Arrays.asList(matcher.group(2).split("\\n")));
                        break;
                    case "click_commands":
                        List<Pair<String, ClickType>> commands = new ArrayList<>();
                        for(String s : matcher.group(2).split("\\n"))
                            commands.add(new Pair<>(s, null));
                        stack.setClickCommands(commands);
                        break;
                    case "slot":
                        slot = Integer.parseInt(matcher.group(2));
                        break;
                }
            }
            if(!MenuArchitect.MENU.getHolder().containsKey(player)) throw new ComponentException("The [add_item] component MUST be inside in a menu!");
            MenuArchitect.MENU.getHolder().get(player).addItem(slot, stack);
            return TMLState.COMPLETED;
        }
        return TMLState.NOT_A_MATCH;
    }

}
