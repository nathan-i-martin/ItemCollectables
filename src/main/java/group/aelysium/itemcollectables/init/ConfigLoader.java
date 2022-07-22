package group.aelysium.itemcollectables.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.gui.BagViewer;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collectible.models.Collectable;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import group.aelysium.itemcollectables.lib.generic.PluginType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

import java.util.Map;

public class ConfigLoader {

    public static boolean loadRootConfig(Map<String, JsonObject> configs, ItemCollectables itemCollectables, MySQL mySQL) {
        ItemCollectables.log("> Preparing config.json...");

        configs.put("config", ItemCollectables.createCustomConfig("config.json"));

        ItemCollectables.log("> > Parsing config.json...");

        {
            try {
                ItemCollectables.log("> > Loading database info...");
                JsonObject databaseJSON = configs.get("config").getAsJsonObject("database");
                assert !databaseJSON.isJsonObject() : "You must provide database information!";

                String host =       databaseJSON.get("host").getAsString();
                int    port =       databaseJSON.get("port").getAsInt();
                String database =   databaseJSON.get("database").getAsString();
                String user =       databaseJSON.get("user").getAsString();
                String password =   databaseJSON.get("password").getAsString();

                mySQL.setConnection(host, port, database, user, password);

                ItemCollectables.log("> > Loading plugin type info...");
                String pluginType = configs.get("config").get("type").getAsString();

                ItemCollectables.log("> > Setting plugin type...");
                itemCollectables.setPluginType(PluginType.valueOf(pluginType));

                itemCollectables.setItemSpawnDelay(configs.get("config").get("seconds-between-item-spawn-attempt").getAsInt() * 20);
            } catch (Exception e) {
                ItemCollectables.log("Unable to register config.yml! Make sure you are using valid json!");
                Bukkit.getPluginManager().disablePlugin(ItemCollectables.getProvidingPlugin(ItemCollectables.class));
                return false;
            }
        }
        ItemCollectables.log("Finished preparing config.json!");
        return true;
    }

    public static boolean saveCollectables(Map<String, JsonObject> configs, MySQL mySQL) throws ExceptionInInitializerError {
        ItemCollectables.log("> Registering collectables.json...");

        configs.put("collectables", ItemCollectables.createCustomConfig("collectables.json"));

        ItemCollectables.log("> > Parsing collectables.json...");
        // Get screens config
        JsonObject collectiblesJSON = configs.get("collectables").getAsJsonObject("collectables");
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
                    double activeRadius = collectibleLocation.get("active-radius").getAsDouble();

                    try {
                        Material material = Material.getMaterial(collectibleJSON.get("material").getAsString());
                        boolean isEnchanted = collectibleJSON.get("enchanted").getAsBoolean();
                        boolean isGlowing = collectibleJSON.get("glowing").getAsBoolean();
                        Integer customModelData = collectibleJSON.get("custom-model-data").getAsInt();

                        JsonObject guiJSON = collectibleJSON.getAsJsonObject("gui");
                        String lore = guiJSON.get("lore").getAsString();
                        Integer guiSlotIndex = guiJSON.get("slot-index").getAsInt();
                        String familyName = collectibleJSON.get("family-name").getAsString();

                        ItemCollectables.log("> > > Saving collectables to database...");
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
                        throw new ExceptionInInitializerError();
                    }

                } catch (Exception e) {
                    throw new ExceptionInInitializerError();
                }
            }
        }
        ItemCollectables.log("Successfully registered collectables.json!");
        return true;
    }

    public static boolean saveFamilies(Map<String, JsonObject> configs, MySQL mySQL) throws ExceptionInInitializerError {
        ItemCollectables.log("> Registering families.json...");

        configs.put("families", ItemCollectables.createCustomConfig("families.json"));

        ItemCollectables.log("> > Parsing families.json...");
        try {
            // Get screens config
            JsonObject familiesJSON = configs.get("families").getAsJsonObject("families");
            assert !familiesJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets? Failed before parsing families!";
            {
                ItemCollectables.log("> > Registering families...");
                for (Map.Entry<String, JsonElement> familyEntry : familiesJSON.entrySet()) {
                    JsonObject familyJSON = familyEntry.getValue().getAsJsonObject();
                    assert !familyJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets? Failed while parsing families!";

                    JsonObject familyItemJSON = familyJSON.get("icon").getAsJsonObject();
                    Material familyItemMaterial = Material.getMaterial(familyItemJSON.get("material").getAsString());
                    Integer familyItemCMD = familyItemJSON.get("custom-model-data").getAsInt();

                    JsonObject missingGUIItemJSON = familyJSON.get("missing-collectable").getAsJsonObject();
                    Material missingGUIItemMaterial = Material.getMaterial(missingGUIItemJSON.get("material").getAsString());
                    Integer missingGUIItemCMD = missingGUIItemJSON.get("custom-model-data").getAsInt();

                    Integer guiRows = familyJSON.get("gui-rows").getAsInt();

                    ItemCollectables.log("> > > Saving families to database...");

                    Family.save(
                            mySQL,
                            familyEntry.getKey(),
                            guiRows,
                            familyItemMaterial,
                            familyItemCMD,
                            missingGUIItemMaterial,
                            missingGUIItemCMD
                    );
                }
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError();
        }
        ItemCollectables.log("Successfully registered families.json!");
        return true;
    }

    public static boolean loadGUIs() throws ExceptionInInitializerError {
        ItemCollectables.log("Preparing Family GUI interfaces...");
        Family.getAll().forEach(family -> {
            //family.bindGUI(BagViewer.constructNew("Collectables", family));
        });
        ItemCollectables.log("Finished preparing GUI interfaces!");
        return true;
    }
}
