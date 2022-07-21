package group.aelysium.itemcollectables.gui;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import group.aelysium.itemcollectables.lib.gui.models.GUI;
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
