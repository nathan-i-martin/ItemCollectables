package group.aelysium.itemcollectables.lib.gui.models;

import group.aelysium.itemcollectables.ItemCollectables;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GUI implements InventoryHolder {
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

    public static ItemStack createItem(Material material, boolean isEnchanted, Integer customModelData, String name, String lore) {
        ItemStack itemStack = new ItemStack(material,1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(!(customModelData == null)) itemMeta.setCustomModelData(customModelData);
        if(isEnchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if(!name.isEmpty()) {
            itemMeta.displayName(Component.text(name));
        }
        if(!lore.isEmpty()) {
            List<String> loreList = List.of(lore.split(","));
            List<Component> loreComponents = new ArrayList<>();
            loreList.forEach(entry -> {
                loreComponents.add(Component.text(entry));
            });
            itemMeta.lore(loreComponents);
        }
        if(isEnchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;

    }
}