package group.aelysium.itemcollectables.gui;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import group.aelysium.itemcollectables.lib.collector.models.Collector;
import group.aelysium.itemcollectables.lib.gui.models.GUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BagSelector extends GUI {
    public BagSelector(String title, int rows, Player player) {
        super(title,rows,player);
    }

    @Override
    public void execute(InventoryClickEvent event, ItemCollectables itemCollectables) {
        event.setCancelled(true);

        PlainComponentSerializer plain = PlainComponentSerializer.plain();
        String inventoryName = plain.serialize(event.getView().title());

        if(!Objects.equals(inventoryName, "Bag Selector")) return;

        final Player player = (Player) event.getWhoClicked();
        final int clickedSlot = event.getRawSlot();
        final ItemStack clickedItem = event.getCurrentItem();

        if(clickedItem == null) return;

        Component itemDisplayName = clickedItem.getItemMeta().displayName();
        if(itemDisplayName == null) {
            ItemCollectables.log("Null item name in Bag Selector!");
            return;
        }

        String itemName = plain.serialize(itemDisplayName);

        Family family = Family.find(itemName);
        if(family == null) return;

        Collector collector = Collector.find(player.getUniqueId());
        if(collector == null) return;

        family.openCollectorBagGUI(player, collector, family).openInventory(player);
    }

    public static BagSelector constructNew(Player player) {
        BagSelector gui = new BagSelector(
                "Bag Selector",
                1,
                player
        );

        final int[] index = {0};
        Family.getAll().forEach(family -> {
            gui.setItem(index[0], family.item);
            index[0]++;
        });

        return gui;
    }
}
