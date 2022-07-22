package group.aelysium.itemcollectables.lib.collector.models;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collectible.models.Bag;
import group.aelysium.itemcollectables.lib.collectible.models.Collectable;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Collector {
    private static final List<Collector> registeredCollectors = new ArrayList<>();

    public UUID uuid;
    public List<Bag> bags = new ArrayList<>();

    public Collector(UUID uuid) {
        this.uuid = uuid;
    }

    public static void saveCollectableInBag(MySQL mySQL, UUID uuid, String collectableName) throws SQLException {
        Connection conn = mySQL.getConnection();

        PreparedStatement request = conn.prepareStatement(
                "REPLACE INTO " +
                        "player_collectables(uuid, collectable_name) " +
                        "VALUES(?, ?);"
        );
        request.setString(1,uuid.toString());
        request.setString(2,collectableName);
        request.execute();
    }

    public static Collector getRemote(MySQL mySQL, UUID uuid) {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement(
                    "SELECT * FROM player_collectables t1 " +
                            "INNER JOIN collectables t2 " +
                            "USING(collectable_name) " +
                            "AND t1.uuid = \"?\";"
            );
            request.setString(1,uuid.toString());
            ResultSet response = request.executeQuery();

            Collector collector = new Collector(uuid);

            if(!response.next()) return null;

            while (response.next()) {
                Family family = Family.find(response.getString("family-name"));
                if(family == null) continue;

                Bag bag = collector.findBag(response.getString("family-name"));
                if(bag == null) {
                    bag = new Bag(family);
                    collector.holdBag(bag);
                }

                Collectable collectable = family.findCollectable(response.getString("collectable-name"));
                if(collectable == null) continue;

                bag.add(collectable.name);
            }

            return collector;
        } catch (SQLException e) {
            return null;
        }
    }

    public static void delete(MySQL mySQL, UUID uuid) {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement(
                    "DELETE FROM player_collectables WHERE uuid='?';"
            );
            request.setString(1,uuid.toString());
            request.execute();
        } catch (SQLException e) {
            ItemCollectables.log("Unable to empty that player's bags!");
        }
    }

    public static Collector getReliably(UUID uuid, MySQL mySQL) {
        Collector collector = Collector.find(uuid);
        if(collector == null) {
            collector = Collector.getRemote(mySQL, uuid);

            if(collector == null) collector = new Collector(uuid);

            Collector.register(collector);
        }
        return collector;
    }

    /**
     * Give a new bag to this collector
     * @param bag The back to give
     * @return bag
     */
    public Bag holdBag(Bag bag) {
        this.bags.add(bag);
        return bag;
    }

    /**
     * Find a bag that was given to this Collector
     * @param family The family that the bag is associated with
     * @return A Bag or `null` if none is found
     */
    public Bag findBag(Family family) {
        Optional<Bag> response = this.bags.stream().filter(bag -> Objects.equals(bag.family, family)).findFirst();
        return response.orElse(null);
    }

    /**
     * Find a bag that was given to this Collector
     * @param familyName The family that the bag is associated with
     * @return Bag
     */
    public Bag findBag(String familyName) {
        Family family = Family.find(familyName);
        if(family == null) return null;
        Optional<Bag> response = bags.stream().filter(bag -> Objects.equals(bag.family, family)).findFirst();
        return response.orElse(null);
    }

    /**
     * Check if this collector is already registered
     * @param family The family that the bag is associated with
     * @return boolean
     */
    public boolean hasBag(Family family) {
        Optional<Bag> response = bags.stream().filter(bag -> Objects.equals(bag.family, family)).findFirst();
        if(response.isPresent()) return true;
        return false;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }


    public static Collector register(Collector collector) {
        registeredCollectors.add(collector);
        return collector;
    }

    public static void unRegister(Collector collector) {
        registeredCollectors.remove(collector);
    }

    /**
     * Remove a collectible from this family category
     * @param uuid The UUID of the collector to remove
     */
    public static void remove(UUID uuid) {
        Collector collector = Collector.find(uuid);
        if(collector == null) return;
        registeredCollectors.remove(collector);
    }

    /**
     * Check if this collector is already registered
     * @param uuid The UUID of the collector to look for
     * @return boolean
     */
    public static boolean contains(UUID uuid) {
        Optional<Collector> response = registeredCollectors.stream().filter(collector -> Objects.equals(collector.uuid, uuid)).findFirst();
        return response.isPresent();
    }

    /**
     * Find a collectible inside of this family
     * @param uuid The UUID of the collector to get
     * @return A Collectible or `null` if none is found
     */
    public static Collector find(UUID uuid) {
        Optional<Collector> response = registeredCollectors.stream().filter(collector -> Objects.equals(collector.uuid, uuid)).findFirst();
        return response.orElse(null);
    }
}
