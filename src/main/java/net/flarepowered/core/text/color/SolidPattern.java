package net.flarepowered.core.text.color;

import net.flarepowered.core.text.other.ColorUtils;

import java.util.regex.Matcher;

public class SolidPattern implements ColorPattern {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("&#([0-9A-Fa-f]{6})");

    /**
     * Applies a solid RGB color to the provided String.
     * Output might be the same as the input if this pattern is not present.
     *
     * @param string The String to which this pattern should be applied to
     * @return The new String with applied pattern
     */
    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String color = matcher.group(1);
            string = string.replace(matcher.group(), ColorUtils.getColor(color) + "");
        }
        return string;
    }
}
