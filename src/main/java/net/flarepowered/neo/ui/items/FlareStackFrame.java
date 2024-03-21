package net.flarepowered.neo.ui.items;

import net.flarepowered.neo.ui.items.FlareStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlareStackFrame {

    private FlareStack stack;

    public FlareStackFrame() { stack = new FlareStack(); }

    public FlareStackFrame setDisplayName(String string) {
        stack.setDisplayName(string);
        return this;
    }

    public FlareStackFrame setLore(String... lore) {
        stack.setLore(Arrays.stream(lore).collect(Collectors.toList()));
        return this;
    }

    public FlareStackFrame setLore(List<String> lore) {
        stack.setLore(lore);
        return this;
    }

    public FlareStackFrame setCustomModelData(int data) {
        stack.setCustomModelData(data);
        return this;
    }

    public FlareStackFrame setGlow(boolean glow) {
        stack.setGlow(glow);
        return this;
    }

    public FlareStackFrame setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public FlareStackFrame setMaterial(String material) {
        stack.setMaterial(FlareMaterial.wrapFromString(material));
        return this;
    }

    public FlareStack build() {
        return stack;
    }

}
