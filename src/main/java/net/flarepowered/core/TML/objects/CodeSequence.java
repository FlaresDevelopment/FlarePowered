package net.flarepowered.core.TML.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CodeSequence {

    public static List<String> runAndCompile(List<String> st) {
        List<String> formattedCode = new ArrayList<>();
        String analyzing = "none";
        StringBuilder working = new StringBuilder();
        for(String s : st) {
            s = s.replaceFirst("^\\s*", "");
            if(analyzing.equalsIgnoreCase("none"))
                switch (s.toLowerCase(Locale.ROOT)) {
                    case "if":
                        analyzing = "if";
                        working.append("{if}");
                        continue;
                    default:
                        formattedCode.add(s);
                        break;
                }
            switch (analyzing) {
                case "if":
                    if(s.equalsIgnoreCase("do")) {
                        working.append("{do}");
                        analyzing = "do";
                    } else
                        working.append(s).append("{|}");
                    break;
                case "do":
                    if(s.equalsIgnoreCase("else")) {
                        working.append("{/do}").append("{else}");
                        analyzing = "else";
                    } else if(s.equalsIgnoreCase("end")) {
                        working.append("{/do}{else}null{/else}{end}");
                        formattedCode.add(working.toString());
                        analyzing = "none";
                    } else
                        working.append(s).append("{|}");
                    break;
                case "else":
                    if(s.equalsIgnoreCase("end")) {
                        working.append("{/else}");
                        analyzing = "end";
                        working.append("{end}");
                        formattedCode.add(working.toString());
                        break;
                    } else
                        working.append(s).append("{|}");
                    break;
                case "end":
                    working.append("{end}");
                    formattedCode.add(working.toString());
                    break;
            }
        }
        return formattedCode;
    }
}
