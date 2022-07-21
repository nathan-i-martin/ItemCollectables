package group.aelysium.itemcollectables.lib.collectible.models;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.MySQL;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class Collectable {
    public String name;
    public String lore;
    public Location location;
    public ItemStack item;
    public Material material;
    public boolean isGlowing = false;
    public boolean isEnchanted = false;
    public double activeRadius = 32.0;
    public Integer guiSlotIndex = 53;
    public Integer customModelData = 0;

    public Collectable(String name, String lore, Material material, Location location, double activeRadius, boolean isGlowing, boolean isEnchanted, Integer guiSlotIndex, Integer customModelData) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.location = location;

        if(activeRadius < 32.0) this.activeRadius = activeRadius;

        this.isGlowing = isGlowing;
        this.isEnchanted = isEnchanted;


        if(guiSlotIndex < 53) this.guiSlotIndex = guiSlotIndex;

        this.customModelData = customModelData;

        this.item = Collectable.createItem(this.material, this.isEnchanted, this.customModelData, this.name, this.lore);
    }

    public static void save(MySQL mySQL, String name, String lore, Material material, Location location, double activeRadius, boolean isGlowing, boolean isEnchanted, Integer guiSlotIndex, Integer customModelData, String familyName) {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement(
                    "INSERT INTO " +
                            "collectables(name, lore, gui_slot_index, material, x, y, z, world, active_radius, enchanted, glowing, custom_model_data, family_name)" +
                            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
            );
            request.setString(0,name);
            request.setString(1,lore);
            request.setInt   (2,guiSlotIndex);
            request.setString(3,material.toString());
            request.setDouble(4,location.getX());
            request.setDouble(5,location.getY());
            request.setDouble(6,location.getZ());
            request.setString(7,location.getWorld().toString());
            request.setDouble(8,activeRadius);
            request.setBoolean(9,isGlowing);
            request.setBoolean(10,isEnchanted);
            request.setDouble(11,customModelData);
            request.setString(12,familyName);
            request.execute();
        } catch (SQLException e) {
            ItemCollectables.log("Unable to save Collectable: "+name);
        }
    }

    public void render(Family family) {
        Collection<Player> players = location.getNearbyEntitiesByType(Player.class, this.activeRadius);

        if(players.isEmpty()) return;

        while(players.iterator().hasNext()) {
            Player player = players.iterator().next();
        }

        Item item = this.location.getWorld().dropItem(location,this.item);
             if(this.isGlowing) item.setGlowing(true);
             item.setPickupDelay(30);

             item.setMetadata("collectible-name",   new FixedMetadataValue(ItemCollectables.getProvidingPlugin(ItemCollectables.class),this.name));
             item.setMetadata("collectible-family", new FixedMetadataValue(ItemCollectables.getProvidingPlugin(ItemCollectables.class),family.name));
    }

    public static ItemStack createItem(Material material, boolean isEnchanted, Integer customModelData, String title, String lore) {
        ItemStack itemStack = new ItemStack(material,1);
        ItemMeta itemMeta = itemStack.getItemMeta();
            if(!(customModelData == null)) itemMeta.setCustomModelData(customModelData);
        if(isEnchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if(!title.isEmpty()) {
            itemMeta.displayName(Component.text(title));
        }
        if(!lore.isEmpty()) {
            itemMeta.displayName(Component.text(lore));
        }
        if(isEnchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

            itemStack.setItemMeta(itemMeta);

        return itemStack;

    }
}
