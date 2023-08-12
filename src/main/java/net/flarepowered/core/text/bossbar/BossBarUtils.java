package net.flarepowered.core.text.bossbar;

import net.flarepowered.FlarePowered;
import net.flarepowered.core.text.StringUtils;
import net.flarepowered.core.text.bossbar.schematics.BossbarSchematic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarUtils implements Listener {

    public HashMap<UUID, Map<String, BossBarObject>> bossbars = new HashMap<>();

    public void runner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : bossbars.keySet()) {
                    for (BossBarObject object : bossbars.get(uuid).values()) {
                        if(object.screenTime != -1) {
                            if(object.screenTime > object.timePassed) {
                                object.timePassed ++;
                                if(object.enableDecreaseAnimation)
                                    object.bossBar.setProgress((double) object.timePassed /object.screenTime);
                            } else {
                                removeBossBarToPlayer(object.owner, object.id);
                            }
                        }
                        if(!object.update)
                            continue;
                        object.update();
                    }
                }
            }
        }.runTaskTimerAsynchronously(FlarePowered.LIB.getPlugin(), 0, 10);
    }

    public void addBossBarToPlayer(Player player, String id, BossbarSchematic bossbarSchematic) {
        if(bossbars.containsKey(player.getUniqueId())) {
            /* If there is other bossbars */
            if(bossbars.get(player.getUniqueId()).containsKey(id))
                return;
            bossbars.get(player.getUniqueId()).put(id, bossbarSchematic.toBossBarObject(player));
            BossBarObject object = bossbars.get(player.getUniqueId()).get(id);
            object.id = id;
            object.buildBossBar(player);
            object.addToPlayer();
        } else {
            bossbars.put(player.getUniqueId(), new HashMap<>());
            bossbars.get(player.getUniqueId()).put(id, bossbarSchematic.toBossBarObject(player));
            BossBarObject object = bossbars.get(player.getUniqueId()).get(id);
            object.id = id;
            object.buildBossBar(player);
            object.addToPlayer();
        }
    }

    public void removeBossBarToPlayer(Player player, String id) {
        if(bossbars.containsKey(player.getUniqueId())) {
            /* If there is other bossbars */
            if(!bossbars.get(player.getUniqueId()).containsKey(id))
                return;
            bossbars.get(player.getUniqueId()).get(id).removeFromPlayer();
            bossbars.get(player.getUniqueId()).remove(id);
        }
        if(bossbars.get(player.getUniqueId()).isEmpty())
            bossbars.remove(player.getUniqueId());
    }

    public void clearBossBarsFromPlayer(Player player) {
        if(bossbars.containsKey(player.getUniqueId())) {
            /* If there is other bossbars */
            bossbars.get(player.getUniqueId()).values().forEach(BossBarObject::removeFromPlayer);
            bossbars.get(player.getUniqueId()).clear();
            bossbars.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent event) {
        clearBossBarsFromPlayer(event.getPlayer());
    }

}
