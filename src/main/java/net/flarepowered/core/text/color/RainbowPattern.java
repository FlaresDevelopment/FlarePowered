package net.flarepowered.core.text.color;

import net.flarepowered.core.text.other.ColorUtils;

import java.awt.*;
import java.util.Random;
import java.util.regex.Matcher;

public class RainbowPattern implements ColorPattern {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)<rainbow>(.*?)</rainbow>");

    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            StringBuilder newContent = new StringBuilder();
            Random rand = new Random();
            for(int i = 0; i < matcher.group(1).length(); i++) {
                newContent.append("&").append(String.format("#%06x", rand.nextInt(0xffffff + 1))).append(matcher.group(1).charAt(i));
            }
            string = string.replace(matcher.group(), ColorUtils.process(newContent.toString()));
        }
        return string;
    }

}
