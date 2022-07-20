package group.aelysium.itemcollectibles.lib.collectible.models;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Family {
    private static List<Family> registeredFamilies;

    public String name;
    private List<Collectible> collectibles;

    public Family(String name) {
        this.name = name;
    }

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
    public static Family register(String name) {
        Family family = new Family(name);
        registeredFamilies.add(family);
        return family;
    }


    /**
     * Categorize a collectible as part of this family
     * @param collectible The collectible to add
     */
    public void addCollectible(Collectible collectible) {
        this.collectibles.add(collectible);
    }

    /**
     * Remove a collectible from this family category
     * @param name The name of the collectible to remove
     */
    public void removeCollectible(String name) {
        Collectible collectible = this.findCollectible(name);
        if(collectible == null) return;
        this.collectibles.remove(collectible);
    }

    /**
     * Check if the family category contains this collectible
     * @param name The name of the collectible to look for
     * @return boolean
     */
    public boolean containsCollectible(String name) {
        Optional<Collectible> response = this.collectibles.stream().filter(collectible -> Objects.equals(collectible.name, name)).findFirst();
        return response.isPresent();
    }

    /**
     * Find a collectible inside of this family
     * @param name The name of the collectible to get
     * @return A Collectible or `null` if none is found
     */
    public Collectible findCollectible(String name) {
        Optional<Collectible> response = this.collectibles.stream().filter(collectible -> Objects.equals(collectible.name, name)).findFirst();
        return response.orElse(null);
    }
}
