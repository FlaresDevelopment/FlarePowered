package net.flarepowered.core.ui.objects;

import net.flarepowered.core.ui.item.ItemStructure;

import java.util.ArrayList;
import java.util.List;

public class Slot {

    private List<ItemStructure> content = new ArrayList<>();
    long updateRate = 1 * 1000;
    long lastUpdate;
    int current = 0;

    public ItemStructure getItemStructure() {
        //FIXME
        //-Check working xd
        if(lastUpdate == 0)
            return content.get(0);
        if(current > content.size()) {
            current = 0;
            lastUpdate = 0;
            return content.get(0);
        }
        if(lastUpdate < System.currentTimeMillis() + updateRate)
            return content.get(current);
        else {
            lastUpdate = System.currentTimeMillis();
            return content.get(++current);
        }
    }

    public List<ItemStructure> getContent() {
        return content;
    }

    public void addContent(ItemStructure itemStructure) {
        this.content.add(itemStructure);
    }
}
