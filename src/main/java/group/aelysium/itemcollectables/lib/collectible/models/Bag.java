package group.aelysium.itemcollectables.lib.collectible.models;

import java.util.List;

public class Bag {
    public List<String> collectibles;
    public Family family;

    public Bag(Family family) {
        this.family = family;
    }

    /**
     * Check if the back has a collectible in it
     * @param name Name of the collectible to search for
     * @return `boolean`
     */
    public boolean contains(String name) {
        return this.collectibles.contains(name);
    }

    /**
     * Add a collectible to the bag
     * @param name Name of the collectible to add
     */
    public void add(String name) {
        this.collectibles.add(name);
    }

    /**
     * Remove a collectible from the bag
     * @param name Name of the collectible to remove
     */
    public void remove(String name) {
        this.collectibles.remove(name);
    }
}
