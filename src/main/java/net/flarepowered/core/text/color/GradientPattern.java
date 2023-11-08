package net.flarepowered.core.text.color;

import net.flarepowered.core.text.other.ColorUtils;

import java.awt.*;
import java.util.regex.Matcher;

public class GradientPattern implements ColorPattern {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)<gradient:#([0-9A-Fa-f]{6})-#([0-9A-Fa-f]{6})>(.*?)</gradient>");

    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String start = matcher.group(1);
            String end = matcher.group(2);
            String content = matcher.group(3);
            string = string.replace(matcher.group(), ColorUtils.color(content, new Color(Integer.parseInt(start, 16)), new Color(Integer.parseInt(end, 16))));
        }
        return string;
    }

}
