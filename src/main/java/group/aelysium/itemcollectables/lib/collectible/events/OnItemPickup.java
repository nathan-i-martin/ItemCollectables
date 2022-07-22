package group.aelysium.itemcollectables.lib.collectible.events;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collectible.models.Bag;
import group.aelysium.itemcollectables.lib.collector.models.Collector;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Objects;

import static group.aelysium.itemcollectables.ItemCollectables.log;

public class OnItemPickup implements Listener {
    private ItemCollectables itemCollectables;
    private MySQL mySQL;

    public OnItemPickup(ItemCollectables itemCollectables, MySQL mySQL) {
        this.itemCollectables = itemCollectables;
        this.mySQL = mySQL;
    }

    // Check for clicks on items
    @EventHandler
    public void onItemPickup(final PlayerAttemptPickupItemEvent event) {
        Item item = event.getItem();
        Player player = event.getPlayer();

        if(!OnItemPickup.verify(item)) return;

        try {
            Material itemMaterial = Objects.requireNonNull(item.getItemStack().getData()).getItemType();
            player.getInventory().remove(itemMaterial);
        } catch (Exception e) {}

        String itemName   = item.getMetadata("collectible-name").get(0).asString();
        String familyName = item.getMetadata("collectible-family").get(0).asString();

        Collector collector = Collector.getReliably(player.getUniqueId(), mySQL);

        if(OnItemPickup.handleCollector(this.mySQL, itemName, familyName, collector)) {
            player.sendMessage(ChatColor.AQUA + "You found a collectable! Use "+ChatColor.BLUE+"/bag "+ChatColor.AQUA+"to check it out!");
        } else {
            player.sendMessage(ChatColor.GRAY + "It seems you've already found this collectable...");
        }

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

    static boolean handleCollector(MySQL mySQL, String collectableName, String familyName, Collector collector) {
        Family family = Family.find(familyName);
        if(family == null) {
            ItemCollectables.log("There is no family with the name: "+ familyName +"!");
            return false;
        }

        Bag bag = collector.findBag(familyName);
        if(bag == null) bag = collector.holdBag(new Bag(family));

        if(bag.contains(collectableName)) return false;

        try {
            Collector.saveCollectableInBag(mySQL, collector.uuid, collectableName);
            bag.add(collectableName);
        } catch (Exception e) {
            ItemCollectables.log("Unable to save item: "+collectableName+" to "+collector.getPlayer().getName()+"'s bag: "+familyName);
        }

        return true;
    }
}
