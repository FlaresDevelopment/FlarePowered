package net.flarepowered.core.text.other;

import net.flarepowered.core.text.Message;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatterUtils {

    /*
     * This is a component parser the style is inspired from the library MiniMessageAPI by https://kyori.net/
     * A tag is a component wrote in text
     *
     * Tags:
     *
     * <click:(OPEN_URL/OPEN_FILE/RUN_COMMAND/SUGGEST_COMMAND/CHANGE_PAGE/COPY_TO_CLIPBOARD):"content">text</click>
     * <hover:"content %nl% as a new line">text</hover>
     *
     * Combined Tags:
     *
     * <click:..."content">text <hover:"hover">hover here </hover></click>
     * <hover:"content">hover, <click:OPEN_URL:"https://google.com">click here</click></hover>
     *
     * */

    static final Pattern tag = Pattern.compile("<(\\w+):(([^>]+):)?[\"|']([^\"']+)[\"|']>(.*?)<\\/\\1>");

    public static BaseComponent[] process(String s, Player player) {
        Matcher matcher = tag.matcher(s);
        List<BaseComponent> comp = new ArrayList<>();
        String second = s;
        while(matcher.find()) {
            ComponentBuilder secondary = new ComponentBuilder();
            String[] store = second.split(matcher.group());
            second = store.length >= 2 ? store[1] : "";

            secondary.append(processComponent(store.length < 1 ? "" : store[0], matcher, player));

            comp.addAll(Arrays.asList(secondary.create()));

        }
        comp.addAll(Arrays.asList(TextComponent.fromLegacyText(Message.format(second, player))));
        return comp.toArray(new BaseComponent[0]);
    }

    private static BaseComponent[] processComponent(String content, Matcher expression, Player player) {
        ComponentBuilder builder = new ComponentBuilder();

        builder.append(TextComponent.fromLegacyText(Message.format(content, player)));

        builder.append(parser(expression, player).create());

        return builder.create();
    }

    private static BaseComponent[] processComponent(String content, Player player) {
        ComponentBuilder builder = new ComponentBuilder();
        Matcher matcher = tag.matcher(content);
        System.out.println(content);
        if(!matcher.find())
            return builder.append(TextComponent.fromLegacyText(Message.format(content, player))).create();
        String[] context = content.split(matcher.group());

        builder.append(TextComponent.fromLegacyText(Message.format(context[0], player)));

        builder.append(parser(matcher, player).create());

        return builder.create();
    }

    private static ComponentBuilder parser(Matcher matcher, Player player) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        switch (matcher.group(1).toLowerCase(Locale.ROOT)) {
            case "click":
                componentBuilder.event(new ClickEvent(ClickEvent.Action.valueOf(matcher.group(3).toUpperCase()), matcher.group(4)));
                break;
            case "hover":
                List<Content> hoverContent = new ArrayList<>();
                Arrays.asList(matcher.group(4).split("%nl%")).forEach(s -> {
                    hoverContent.add(new Text(TextComponent.fromLegacyText(Message.format(s.trim(), player))));
                    hoverContent.add(new Text("\n"));
                });
                hoverContent.remove(hoverContent.size() - 1);
                componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverContent));
                break;
        }
        componentBuilder.append(processComponent(matcher.group(5), player));
        return componentBuilder;
    }

}
