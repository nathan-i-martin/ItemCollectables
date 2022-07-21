package group.aelysium.itemcollectibles.init;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import group.aelysium.itemcollectibles.ItemCollectables;
import group.aelysium.itemcollectibles.gui.BagViewer;
import group.aelysium.itemcollectibles.lib.MySQL;
import group.aelysium.itemcollectibles.lib.collectible.models.Collectable;
import group.aelysium.itemcollectibles.lib.collectible.models.Family;
import group.aelysium.itemcollectibles.lib.generic.PluginType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;

public class ConfigLoader {

    public static boolean loadRootConfig(Map<String, JsonObject> configs, ItemCollectables itemCollectables) {
        ItemCollectables.log("Preparing config.json...");
        configs.put("config", ItemCollectables.createCustomConfig("config.json"));
        {
            JsonObject playersJSON = configs.get("database").getAsJsonObject();
            assert !playersJSON.isJsonObject() : "You must provide database information!";

            try {
                String host =       playersJSON.get("host").getAsString();
                int    port =       playersJSON.get("port").getAsInt();
                String database =   playersJSON.get("database").getAsString();
                String user =       playersJSON.get("user").getAsString();
                String password =   playersJSON.get("password").getAsString();

                itemCollectables.setMySQL(new MySQL(host, port, database, user, password));

                String pluginType = playersJSON.get("type").getAsString();

                itemCollectables.setPluginType(PluginType.valueOf(pluginType));
            } catch (Exception e) {
                ItemCollectables.log("Unable to register config.yml! Make sure you are using valid json!");
                Bukkit.getPluginManager().disablePlugin(ItemCollectables.getProvidingPlugin(ItemCollectables.class));
            }
        }
        ItemCollectables.log("Finished preparing config.json!");
        return true;
    }

    public static boolean saveCollectables(Map<String, JsonObject> configs, MySQL mySQL) {
        configs.put("collectables", ItemCollectables.createCustomConfig("collectables.json"));

        ItemCollectables.log("> Registering collectables.json...");
        // Get screens config
        JsonObject collectiblesJSON = configs.get("collectables").getAsJsonObject();
        assert !collectiblesJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets?";
        {
            for (Map.Entry<String, JsonElement> collectibleEntry : collectiblesJSON.entrySet()) {
                JsonObject collectibleJSON = collectibleEntry.getValue().getAsJsonObject();
                assert !collectibleJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets?";

                JsonObject collectibleLocation = collectibleJSON.getAsJsonObject("location");
                assert collectibleLocation == null : "'location' can't be null! Failed inside of: " + collectibleEntry.getKey() + "!";

                try {
                    Location location = new Location(
                            Bukkit.getWorld(collectibleLocation.get("world").getAsString()),
                            collectibleLocation.get("x").getAsDouble(),
                            collectibleLocation.get("y").getAsDouble(),
                            collectibleLocation.get("z").getAsDouble()
                    );
                    double activeRadius = collectibleJSON.get("active-radius").getAsDouble();

                    try {
                        Material material = Material.getMaterial(collectibleJSON.get("material").getAsString());
                        boolean isEnchanted = collectibleJSON.get("enchanted").getAsBoolean();
                        boolean isGlowing = collectibleJSON.get("material").getAsBoolean();
                        Integer customModelData = collectibleJSON.get("custom-model-data").getAsInt();

                        JsonObject guiJSON = collectibleJSON.getAsJsonObject("gui");
                        String lore = guiJSON.get("lore").getAsString();
                        Integer guiSlotIndex = guiJSON.get("slot-index").getAsInt();
                        String familyName = guiJSON.get("family-name").getAsString();

                        Collectable.save(
                                mySQL,
                                collectibleEntry.getKey(),
                                lore,
                                material,
                                location,
                                activeRadius,
                                isGlowing,
                                isEnchanted,
                                guiSlotIndex,
                                customModelData,
                                familyName
                        );

                    } catch (Exception e) {
                        ItemCollectables.log("All collectibles must have `material` defined! Failed at: " + collectibleEntry.getKey());
                        Bukkit.getPluginManager().disablePlugin(ItemCollectables.getProvidingPlugin(ItemCollectables.class));
                    }

                } catch (Exception e) {
                    ItemCollectables.log("All collectibles must have location `x` `y` `z` `world` and `active-radius` defined! Failed at: " + collectibleEntry.getKey());
                    Bukkit.getPluginManager().disablePlugin(ItemCollectables.getProvidingPlugin(ItemCollectables.class));
                }
            }
        }
        ItemCollectables.log("Successfully registered collectables.json!");
        return true;
    }

    public static boolean saveFamilies(Map<String, JsonObject> configs, MySQL mySQL) {
        configs.put("families", ItemCollectables.createCustomConfig("families.json"));

        ItemCollectables.log("> Registering families.json...");
        // Get screens config
        JsonObject familiesJSON = configs.get("families").getAsJsonObject();
        assert !familiesJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets? Failed before parsing families!";
        {
            ItemCollectables.log("> > Registering families...");
            for (Map.Entry<String, JsonElement> familyEntry : familiesJSON.entrySet()) {
                JsonObject familyJSON = familyEntry.getValue().getAsJsonObject();
                assert !familyJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets? Failed while parsing families!";

                Integer guiRows = familyJSON.get("gui-rows").getAsInt();

                Family.save(
                        mySQL,
                        familyEntry.getKey(),
                        guiRows
                );
            }
        }
        ItemCollectables.log("Successfully registered families.json!");
        return true;
    }

    public static boolean loadGUIs() {
        ItemCollectables.log("Preparing Family GUI interfaces...");
        Family.getAll().forEach(family -> {
            family.bindGUI(BagViewer.constructNew("Collectables", family));
        });
        ItemCollectables.log("Finished preparing GUI interfaces!");
        return true;
    }
}
