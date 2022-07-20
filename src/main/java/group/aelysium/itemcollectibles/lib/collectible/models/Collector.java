package group.aelysium.itemcollectibles.lib.collectible.models;

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
     * Check if the family category contains this collectible
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
