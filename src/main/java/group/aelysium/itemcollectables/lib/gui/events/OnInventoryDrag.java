package group.aelysium.itemcollectables.lib.gui.events;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.gui.models.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class OnInventoryDrag implements Listener {
    private ItemCollectables itemCollectables;

    public OnInventoryDrag(ItemCollectables itemCollectables) {
        this.itemCollectables = itemCollectables;
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if(!this.verify(event)) return;

            event.setCancelled(true);
    }

    /**
     * Verifies that the current event is valid
     * @param event
     * @return
     */
    static boolean verify(InventoryDragEvent event) {

        //filter out bad events
        if (event.getInventory() == null) return false;

        //check if the inventory is an instance of our menu
        if (event.getInventory().getHolder() instanceof GUI) {
        }

        return true;
    }
}
