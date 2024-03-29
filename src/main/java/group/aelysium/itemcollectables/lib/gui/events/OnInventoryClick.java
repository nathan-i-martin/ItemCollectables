package group.aelysium.itemcollectables.lib.gui.events;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.gui.models.GUI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class OnInventoryClick implements Listener {
    private ItemCollectables itemCollectables;

    public OnInventoryClick(ItemCollectables itemCollectables) {
        this.itemCollectables = itemCollectables;
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {

        if(!OnInventoryClick.verify(event)) return;

        ((GUI) event.getInventory().getHolder()).execute(event,this.itemCollectables);

        event.setCancelled(true);
    }

    /**
     * Verifies that the current event is valid
     * @param event
     * @return
     */
    static boolean verify(InventoryClickEvent event) {
        //check if the inventory is an instance of our menu
        if (!(event.getInventory().getHolder() instanceof GUI)) return false;

        if(event.getInventory().getHolder() == null) return false;

        final ItemStack clickedItem = event.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return false;

        return true;
    }
}
