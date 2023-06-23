package net.flarepowered.core.ui;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.ui.objects.PanelUI;
import net.flarepowered.core.ui.objects.PanelVariables;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class UI {

    private HashMap<Integer, PanelUI> content;
    private PanelUI current;
    private List<PanelVariables> panelVariables;
    private Player holder;

    public UI (Player player) {
        this.holder = player;
        FlarePowered.LIB.getUiController().addToUI(holder.getUniqueId(), this);
    }

    private void showToPlayer() {
        if(current == null) {
            if(content.containsKey(0)) {
                current = content.get(0);
            }
        }
        current.showToPlayer();
    }

    private void updatePanel() {

    }

    private void switchPanel() {

    }

}
