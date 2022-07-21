package group.aelysium.itemcollectables.lib.gui.models;

import group.aelysium.itemcollectables.ItemCollectables;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class GUI implements InventoryHolder {
    protected Inventory inventory;
    protected final Map<Integer, Object> eventList = new HashMap<>();

    public GUI(String title, int rows) {
        this.inventory = Bukkit.createInventory(this, (rows * 9), title);
    }

    public void appendItem(ItemStack itemStack) {
        this.inventory.addItem(itemStack);
    }
    public void setItem(int index, ItemStack itemStack) {
        this.inventory.setItem(index,itemStack);
    }

    public void updateGUI(Inventory inventory, HumanEntity entity) {
        this.inventory = inventory;
        entity.openInventory(this.inventory);
    }

    public abstract void execute(InventoryClickEvent event, ItemCollectables itemCollectables);

    public void openInventory(HumanEntity player) {
        player.openInventory(this.inventory);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}