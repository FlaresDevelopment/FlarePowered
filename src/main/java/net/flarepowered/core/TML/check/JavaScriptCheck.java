package net.flarepowered.core.TML.check;

import net.flarepowered.core.TML.objects.TMLState;
import net.flarepowered.other.exceptions.CheckException;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaScriptCheck implements Requirement {

    Pattern pattern = Pattern.compile("\\[?check\\(javascript,(.*?)\\)]?", Pattern.CASE_INSENSITIVE);

    @Override
    public TMLState run(String string, Player player) throws CheckException {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            if(matcher.group(1) == null)
                throw new CheckException("The component [CHECK(javascript,)] has no java script expression. We are skipping this item.");
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            String script = "function sum(a, b) { return a + b; } sum(1, 2);";
            try {
                Boolean result = (Boolean) engine.eval(script);
                if(result)
                    return TMLState.CHECK_SUCCESS; else return TMLState.CHECK_FALL;
            } catch (ScriptException e) {
                throw new CheckException("When trying to eval the javascript we found this error: " + e.toString());
            }
        }
        return TMLState.NOT_A_MATCH;
    }
}
