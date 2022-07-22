package group.aelysium.itemcollectables.lib.collector.events;

import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collector.models.Collector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {
    private MySQL mySQL;

    public OnPlayerJoin(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    // Check for clicks on items
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();



    }
}
