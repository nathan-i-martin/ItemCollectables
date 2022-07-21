package group.aelysium.itemcollectables.lib.collector;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collectible.models.Bag;
import group.aelysium.itemcollectables.lib.collectible.models.Family;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Collector {
    private static List<Collector> registeredCollectors;

    public UUID uuid;
    public List<Bag> bags;

    public Collector(UUID uuid) {
        this.uuid = uuid;
    }

    public static void saveCollectableInBag(MySQL mySQL, UUID uuid, String collectableName) {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement(
                    "INSERT INTO " +
                            "player_collectables(uuid, collectable_name)" +
                            "VALUES(?, ?);"
            );
            request.setString(0,uuid.toString());
            request.setString(1,collectableName);
            request.execute();
        } catch (SQLException e) {
            ItemCollectables.log("Unable to save Collectable to the players bag!");
        }
    }

    public static void delete(MySQL mySQL, UUID uuid) {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement(
                    "DELETE FROM player_collectables WHERE uuid='?';"
            );
            request.setString(0,uuid.toString());
            request.execute();
        } catch (SQLException e) {
            ItemCollectables.log("Unable to empty that player's bags!");
        }
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



    /**
     * Categorize a collectible as part of this family
     * @param collector The collectible to add
     */
    public static void add(Collector collector) {
        registeredCollectors.add(collector);
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
