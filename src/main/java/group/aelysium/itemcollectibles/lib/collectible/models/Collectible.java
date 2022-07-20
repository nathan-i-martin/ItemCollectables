package group.aelysium.itemcollectibles.lib.collectible.models;

import group.aelysium.itemcollectibles.ItemCollectibles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collection;

public class Collectible {
    public String name;
    public Location location;
    public ItemStack item;
    public boolean isGlowing = false;
    public double activeRange = 32.0;

    public Collectible(String name, Location location, ItemStack item, double activeRange, boolean isGlowing) {
        this.name = name;
        this.location = location;
        this.item = item;

        if(activeRange < 32.0) this.activeRange = activeRange;

        this.isGlowing = isGlowing;
    }

    public void render(Family family) {
        Collection<Player> players = location.getNearbyEntitiesByType(Player.class, this.activeRange);

        if(players.isEmpty()) return;

        while(players.iterator().hasNext()) {
            Player player = players.iterator().next();
        }

        Item item = this.location.getWorld().dropItem(location,this.item);
             if(this.isGlowing) item.setGlowing(true);
             item.setPickupDelay(30);

             item.setMetadata("collectible-name",   new FixedMetadataValue(ItemCollectibles.getProvidingPlugin(ItemCollectibles.class),this.name));
             item.setMetadata("collectible-family", new FixedMetadataValue(ItemCollectibles.getProvidingPlugin(ItemCollectibles.class),family.name));
    }

    public static ItemStack createItem(Material material, boolean isEnchanted, Integer customModelData) {
        ItemStack itemStack = new ItemStack(material,1);
        ItemMeta itemMeta = itemStack.getItemMeta();
            if(!(customModelData == null)) itemMeta.setCustomModelData(customModelData);
            if(isEnchanted) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            itemStack.setItemMeta(itemMeta);

        return itemStack;

    }
}
