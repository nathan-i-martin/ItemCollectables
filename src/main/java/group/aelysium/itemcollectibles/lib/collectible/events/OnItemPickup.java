package group.aelysium.itemcollectibles.lib.collectible.events;

import group.aelysium.itemcollectibles.ItemCollectibles;
import group.aelysium.itemcollectibles.lib.collectible.models.Bag;
import group.aelysium.itemcollectibles.lib.collectible.models.Collector;
import group.aelysium.itemcollectibles.lib.collectible.models.Family;
import group.aelysium.itemcollectibles.lib.gui.models.GUI;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static group.aelysium.itemcollectibles.ItemCollectibles.log;

public class OnItemPickup implements Listener {
    private ItemCollectibles itemCollectibles;

    public OnItemPickup(ItemCollectibles itemCollectibles) {
        this.itemCollectibles = itemCollectibles;
    }

    // Check for clicks on items
    @EventHandler
    public void onItemPickup(final PlayerAttemptPickupItemEvent event) {
        Item item = event.getItem();
        Player player = event.getPlayer();

        if(!OnItemPickup.verify(item)) return;

        String itemName   = item.getMetadata("collectible-name").get(0).asString();
        String familyName = item.getMetadata("collectible-family").get(0).asString();

        if(!Collector.contains(player.getUniqueId())) return;

        if(!OnItemPickup.handleCollector(itemName,familyName,player)) return;

        event.setCancelled(true);
    }

    /**
     * Verifies that the current item is valid
     * @param item The item to verify
     * @return boolean
     */
    static boolean verify(Item item) {
        if(!item.hasMetadata("collectible-name"))
            if(!item.hasMetadata("collectible-family"))
                return false;

        if(!(item.hasMetadata("collectible-name")) &&  (item.hasMetadata("collectible-family"))) {
            log("Item had the `collectible-family` tag but not the `collectible-name` tag! Unable to validate this item!");
            return false;
        }
        if( (item.hasMetadata("collectible-name")) && !(item.hasMetadata("collectible-family"))) {
            log("Item had the `collectible-name` tag but not the `collectible-family` tag! Unable to validate this item!");
            return false;
        }

        return true;
    }

    static boolean handleCollector(String itemName, String familyName, Player player) {
        Collector collector;
        if(Collector.contains(player.getUniqueId())) collector = Collector.find(player.getUniqueId());
        else {
            collector = new Collector(player.getUniqueId());
            Collector.add(collector);
        }

        Family family = Family.find(familyName);
        if(family == null) {
            ItemCollectibles.log("There is no family with the name: "+ familyName +"!");
            return false;
        }

        Bag bag = collector.findBag(family);
        if(bag == null) bag = collector.holdBag(new Bag(familyName));

        bag.add(itemName);

        return true;
    }
}
