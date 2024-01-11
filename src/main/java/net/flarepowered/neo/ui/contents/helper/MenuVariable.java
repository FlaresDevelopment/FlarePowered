package net.flarepowered.neo.ui.contents.helper;

public class MenuVariable<T> {
    private final String id;
    private T content;

    public MenuVariable(String id, T value) {
        this.id = id;
        this.content = value;
    }

    public T getVariable() {
        return content;
    }

    public void setVariable(T value) {
        content = value;
    }

    public String getID() {
        return id;
    }

}
