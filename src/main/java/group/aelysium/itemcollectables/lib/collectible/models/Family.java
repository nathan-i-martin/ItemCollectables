package group.aelysium.itemcollectables.lib.collectible.models;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.gui.models.GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Family {
    private static List<Family> registeredFamilies;

    public String name;
    public Integer guiRows;
    public List<Collectable> collectables;
    private GUI gui;

    public Family(String name, Integer guiRows) {
        this.name = name;
        this.guiRows = guiRows;
    }

    public static void save(MySQL mySQL, String name, Integer guiRows) {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement(
                    "INSERT INTO " +
                            "families(name, gui_rows)" +
                            "VALUES(?, ?);"
            );
            request.setString(0,name);
            request.setInt(1,guiRows);
            request.execute();
        } catch (SQLException e) {
            ItemCollectables.log("Unable to save Family: "+name);
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
     * @param name The name of the family to get
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
}
