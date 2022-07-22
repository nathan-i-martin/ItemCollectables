package group.aelysium.itemcollectables.engine;

import group.aelysium.itemcollectables.lib.collectible.models.Family;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemSpawn extends BukkitRunnable {

    @Override
    public void run() {
        Family.getAll().forEach(family -> {
            family.collectables.forEach(collectable -> {
                collectable.render(family);
            });
        });
    }
}
