package group.aelysium.itemcollectables.lib.collectible.models;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.gui.BagSelector;
import group.aelysium.itemcollectables.gui.BagViewer;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collector.models.Collector;
import group.aelysium.itemcollectables.lib.gui.models.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Family {
    private static final List<Family> registeredFamilies = new ArrayList<>();

    public String name;
    public Integer guiRows;
    public List<Collectable> collectables = new ArrayList<>();
    public ItemStack item;
    public ItemStack itemIfMissingCollectable;
    private GUI gui;

    public Family(String name, Integer guiRows, Material familyItemMaterial, Integer familyItemCMD, Material missingGUIItemMaterial, Integer missingGUIItemCMD) {
        this.name = name;
        this.guiRows = guiRows;

        this.item = GUI.createItem(familyItemMaterial, false, familyItemCMD, this.name, "Click to view");
        this.itemIfMissingCollectable = GUI.createItem(missingGUIItemMaterial, false, missingGUIItemCMD, this.name, "Click to view");
    }

    public static void openFamilySelectorGUI(Player player) {
        BagSelector.constructNew(player).openInventory(player);
    }

    public static void save(MySQL mySQL, String name, Integer guiRows, Material familyItemMaterial, Integer familyItemCMD, Material missingGUIItemMaterial, Integer missingGUIItemCMD) throws ExceptionInInitializerError {
        try {
            Connection connection = mySQL.getConnection();

            PreparedStatement request = connection.prepareStatement(
                    "INSERT INTO " +
                            "families(family_name, gui_rows, family_icon_material, family_icon_CMD, gui_missing_item_material, gui_missing_item_CMD) " +
                            "VALUES(?, ?, ?, ?, ?, ?);"
            );
            request.setString(1,name);
            request.setInt(2,guiRows);
            request.setString(3,familyItemMaterial.toString());
            request.setInt(4,familyItemCMD);
            request.setString(5,missingGUIItemMaterial.toString());
            request.setInt(6,missingGUIItemCMD);
            request.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            ItemCollectables.log("Unable to save Family: "+name);
            throw new ExceptionInInitializerError();
        }
    }

    public static List<Family> getAll() { return registeredFamilies; }

    /**
     * Find a family from the list of registered families
     * @param name The name of the family to get
     * @return A Family or `null` if none is found
     */
    public static Family find(String name) {
        Optional<Family> response = registeredFamilies.stream().filter(collectible -> Objects.equals(collectible.name, name)).findFirst();
        return response.orElse(null);
    }

    /**
     * Find a family from the list of registered families
     * @param family The name of the family to get
     * @return A Family or `null` if none is found
     */
    public static void register(Family family) {
        registeredFamilies.add(family);
    }


    /**
     * Assign a GUI to this family which will be opened anytime someone view's their bag
     * @param gui The gui to set
     */
    public void bindGUI(GUI gui) {
        this.gui = gui;
    }

    /**
     * Categorize a collectable as part of this family
     * @param collectable The collectable to add
     */
    public void addCollectable(Collectable collectable) {
        this.collectables.add(collectable);
    }

    /**
     * Remove a collectible from this family category
     * @param name The name of the collectible to remove
     */
    public void removeCollectale(String name) {
        Collectable collectable = this.findCollectable(name);
        if(collectable == null) return;
        this.collectables.remove(collectable);
    }

    /**
     * Check if the family category contains this collectable
     * @param name The name of the collectable to look for
     * @return boolean
     */
    public boolean containsCollectible(String name) {
        Optional<Collectable> response = this.collectables.stream().filter(collectable -> Objects.equals(collectable.name, name)).findFirst();
        return response.isPresent();
    }

    /**
     * Find a collectable inside of this family
     * @param name The name of the collectable to get
     * @return A Collectable or `null` if none is found
     */
    public Collectable findCollectable(String name) {
        Optional<Collectable> response = this.collectables.stream().filter(collectable -> Objects.equals(collectable.name, name)).findFirst();
        return response.orElse(null);
    }

    public GUI openCollectorBagGUI(Player player, Collector collector, Family family) {
        Bag bag = collector.findBag(family); // If Bag is null, we pass it anyways
        return BagViewer.constructNew(player, bag, family);
    }
}
