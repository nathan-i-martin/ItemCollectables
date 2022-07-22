package group.aelysium.itemcollectables.lib.collectible.models;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.gui.models.GUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

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

        this.item = GUI.createItem(this.material, this.isEnchanted, this.customModelData, this.name, this.lore);
    }

    public static void save(MySQL mySQL, String name, String lore, Material material, Location location, double activeRadius, boolean isGlowing, boolean isEnchanted, Integer guiSlotIndex, Integer customModelData, String familyName) throws ExceptionInInitializerError {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement(
                    "INSERT INTO " +
                            "collectables(collectable_name, lore, gui_slot_index, material, x, y, z, world, active_radius, enchanted, glowing, custom_model_data, family_name)" +
                            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
            );
            request.setString(1,name);
            request.setString(2,lore);
            request.setInt   (3,guiSlotIndex);
            request.setString(4,material.toString());
            request.setDouble(5,location.getX());
            request.setDouble(6,location.getY());
            request.setDouble(7,location.getZ());
            request.setString(8,location.getWorld().getName());
            request.setDouble(9,activeRadius);
            request.setBoolean(10,isGlowing);
            request.setBoolean(11,isEnchanted);
            request.setDouble(12,customModelData);
            request.setString(13,familyName);
            request.execute();
        } catch (SQLException e) {
            ItemCollectables.log("Unable to save Collectable: "+name);
            throw new ExceptionInInitializerError();
        }
    }

    public void render(Family family) {
        Collection<Player> players = location.getNearbyEntitiesByType(Player.class, this.activeRadius);
        if(players.isEmpty()) return;

        Collection<Item> items = location.getNearbyEntitiesByType(Item.class, 1);
        if(!items.isEmpty()) {
            for (Item item : items)
                if (item.hasMetadata("collectible-name") || item.hasMetadata("collectible-family")) return;
        }

        Item item = this.location.getWorld().dropItem(location,this.item);
             if(this.isGlowing) item.setGlowing(true);
             item.setPickupDelay(40);

             item.setMetadata("collectible-name",   new FixedMetadataValue(ItemCollectables.getProvidingPlugin(ItemCollectables.class),this.name));
             item.setMetadata("collectible-family", new FixedMetadataValue(ItemCollectables.getProvidingPlugin(ItemCollectables.class),family.name));

             item.setTicksLived(5000); // Will make the item despawn after 1000 ticks or 50 seconds
    }
}
