package group.aelysium.itemcollectibles.gui;

import group.aelysium.itemcollectibles.ItemCollectables;
import group.aelysium.itemcollectibles.lib.collectible.models.Family;
import group.aelysium.itemcollectibles.lib.gui.models.GUI;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BagViewer extends GUI {
    public BagViewer(String title, int rows) {
        super(title,rows);
    }

    @Override
    public void execute(InventoryClickEvent event, ItemCollectables itemCollectables) {}

    public static BagViewer constructNew(String title, Family family) {
        BagViewer gui = new BagViewer(
                title,
                6
        );

        family.collectables.forEach(collectable -> {
            gui.setItem(collectable.guiSlotIndex,collectable.item);
        });

        return gui;
    }
}
