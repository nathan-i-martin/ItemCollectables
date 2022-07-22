package group.aelysium.itemcollectables.gui;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.collectible.models.Bag;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import group.aelysium.itemcollectables.lib.collector.models.Collector;
import group.aelysium.itemcollectables.lib.gui.models.GUI;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BagViewer extends GUI {
    public BagViewer(String title, int rows, Player player) {
        super(title,rows,player);
    }

    @Override
    public void execute(InventoryClickEvent event, ItemCollectables itemCollectables) {
        event.setCancelled(true);

        PlainComponentSerializer plain = PlainComponentSerializer.plain();
        if(!plain.serialize(event.getView().title()).contains("Viewing Collected")) return;

        final int clickedSlot = event.getRawSlot();

        if(clickedSlot == 0) {
            Family.openFamilySelectorGUI(Bukkit.getPlayer(event.getWhoClicked().getUniqueId()));
        }
    }

    public static BagViewer constructNew(Player player, Bag bag, Family family) {
        BagViewer gui = new BagViewer(
                "Viewing Collected "+family.name,
                family.guiRows,
                player
        );

        gui.setItem(0,GUI.createItem(Material.BARRIER,false,100,"<<< Go back", "Click to go back"));

        family.collectables.forEach(collectable -> {
            if(collectable.guiSlotIndex == 0) {
                ItemCollectables.log("ERROR: The GUI slot-index for: "+collectable.name+" was set to 0! This slot is reserved to the back button. Ignoring...");
                return;
            }

            if(bag == null) {
                gui.setItem(collectable.guiSlotIndex,family.itemIfMissingCollectable);
                return;
            }
            if(bag.collectibles.contains(collectable.name)) {
                gui.setItem(collectable.guiSlotIndex,collectable.item);
                return;
            }

            gui.setItem(collectable.guiSlotIndex,family.itemIfMissingCollectable);
        });

        return gui;
    }
}
