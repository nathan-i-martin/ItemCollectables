package group.aelysium.itemcollectibles.lib.gui.models;

import group.aelysium.itemcollectibles.ItemCollectibles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GUI implements InventoryHolder {
    protected final Player player;
    protected Inventory inventory;
    protected final Map<Integer, Object> eventList = new HashMap<>();

    public GUI(String title, int rows, Player player) {
        this.inventory = Bukkit.createInventory(this, (rows * 9), title);
        this.player = player;
    }

    public void appendItem(ItemStack itemStack) {
        this.inventory.addItem(itemStack);
    }
    public void setItem(int index, ItemStack itemStack) {
        this.inventory.setItem(index,itemStack);
    }

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        if(meta == null) return item;

        meta.setDisplayName(name);

        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createGuiItem(final Material material, final short data, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1, data);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    public void updateGUI(Inventory inventory, HumanEntity entity) {
        this.inventory = inventory;
        entity.openInventory(this.inventory);
    }

    void execute(InventoryClickEvent event, ItemCollectibles itemCollectibles) {}

    public void openInventory(HumanEntity player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}