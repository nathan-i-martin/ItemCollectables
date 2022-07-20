package group.aelysium.itemcollectibles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import group.aelysium.itemcollectibles.lib.collectible.models.Bag;
import group.aelysium.itemcollectibles.lib.collectible.models.Collectible;
import group.aelysium.itemcollectibles.lib.collectible.models.Collector;
import group.aelysium.itemcollectibles.lib.collectible.models.Family;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;

public final class ItemCollectibles extends JavaPlugin implements Listener {

    private DataSource dataSource;
    public Map<String, JsonObject> configs = new HashMap<>();

    @Override
    public void onEnable() {
        registerConfigs();

        registerCommands();

        registerEvents();

        log("Started Successfully!");
    }

    @Override
    public void onDisable() {
        log("Shutting down...");
    }

    /**
     * Register all of the plugin's commands
     */
    public void registerCommands() {

    }

    /**
     * Register all of the plugin's Event Listeners
     */
    public void registerEvents() {
        PluginManager eventManager = getServer().getPluginManager();
    }

    /**
     * Register the plugin's config files
     */
    public void registerConfigs() {
        ItemCollectibles.log("Registering configs...");

        this.configs.put("collectibles", createCustomConfig("collectibles.json"));

        ItemCollectibles.log("> Registering collectibles.json...");
        // Get screens config
        JsonObject collectiblesJSON = this.configs.get("collectibles").getAsJsonObject();
        assert !collectiblesJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets? Failed before parsing families!";
        {
            ItemCollectibles.log("> > Registering families...");
            for (Map.Entry<String, JsonElement> familyEntry : collectiblesJSON.entrySet()) {
                JsonObject familyJSON = familyEntry.getValue().getAsJsonObject();
                assert !familyJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets? Failed while parsing families!";

                Family family = Family.register(familyEntry.getKey());

                ItemCollectibles.log("> > Registering " + family.name + "'s collectibles...");

                for (Map.Entry<String, JsonElement> collectibleEntry : familyJSON.entrySet()) {
                    JsonObject collectibleJSON = collectibleEntry.getValue().getAsJsonObject();
                    assert !collectibleJSON.isJsonObject() : "Your config file is corrupt! Are you sure that you've balanced your brackets? Failed while parsing your collectibles inside of" + family.name + "!";

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

                            Collectible collectible = new Collectible(
                                    collectibleEntry.getKey(),
                                    location,
                                    Collectible.createItem(material, isEnchanted, customModelData),
                                    activeRadius,
                                    isGlowing
                            );

                            family.addCollectible(collectible);

                        } catch (Exception e) {
                            ItemCollectibles.log("All collectibles must have `material` defined! Failed at: " + collectibleEntry.getKey());
                            Bukkit.getPluginManager().disablePlugin(ItemCollectibles.getProvidingPlugin(ItemCollectibles.class));
                        }

                    } catch (Exception e) {
                        ItemCollectibles.log("All collectibles must have location `x` `y` `z` `world` and `active-radius` defined! Failed at: " + collectibleEntry.getKey());
                        Bukkit.getPluginManager().disablePlugin(ItemCollectibles.getProvidingPlugin(ItemCollectibles.class));
                    }
                }

                ItemCollectibles.log("> > " + family.name + "'s collectibles were successfully registered!");
            }
        }
        ItemCollectibles.log("Successfully registered collectibles.json!");
        ItemCollectibles.log("> Registering players.json...");

        this.configs.put("players",createCustomConfig("players.json"));
        {
            // Get screens config
            JsonObject playersJSON = this.configs.get("players").getAsJsonObject();
            assert !playersJSON.isJsonObject() : "players.json is corrupted! You might be able to save it by balancing brackets! BE CAREFUL!!!";

            for(Map.Entry<String, JsonElement> playerEntry : playersJSON.entrySet()) {
                JsonObject playerBags = playerEntry.getValue().getAsJsonObject();
                assert !playerBags.isJsonObject() : "players.json is corrupted! You might be able to save it by balancing brackets! BE CAREFUL!!! Failed at UUID:"+playerEntry.getKey();

                ItemCollectibles.log("> > Registering bags for " + playerEntry.getKey() + "...");
                try {
                    UUID uuid = UUID.fromString(playerEntry.getKey());
                    Collector collector = new Collector(uuid);

                    for(Map.Entry<String, JsonElement> bagEntry : playerBags.entrySet()) {
                        JsonArray bagCollectiblesJSON = bagEntry.getValue().getAsJsonArray();
                        assert !bagCollectiblesJSON.isJsonArray() : "players.json is corrupted! You might be able to save it by balancing brackets! BE CAREFUL!!! Failed at UUID: "+playerEntry.getKey()+" while parsing a bag!";

                        ItemCollectibles.log("> > Registering collectibles for " + playerEntry.getKey() + "'s bag: "+bagEntry.getKey()+"...");

                        Bag bag = new Bag(bagEntry.getKey());

                        bagCollectiblesJSON.forEach(entry -> {
                            Family family = Family.find(bagEntry.getKey());
                            if(family == null) return;

                            String collectibleID = entry.getAsString();
                            if(family.containsCollectible(collectibleID)) bag.add(collectibleID);
                        });

                        collector.holdBag(bag);
                    }
                    ItemCollectibles.log("> Registered " + playerEntry.getKey() + "!");
                } catch (Exception e) {
                    ItemCollectibles.log("Unable to register the collectibles for one of the players.");
                    Bukkit.getPluginManager().disablePlugin(ItemCollectibles.getProvidingPlugin(ItemCollectibles.class));
                }
            }
        }
        ItemCollectibles.log("Finished registering configs!");
    }

    /**
     * Creates a custom config file using a template from resources/config
     * @param configName The name of the config template to get
     */
    private JsonObject createCustomConfig(String configName) {
        File customConfigFile = new File(getDataFolder(), configName); // Load the custom config from the plugins data file
        log("Searching for "+configName);
        if (!customConfigFile.exists()) { // Check if the custom config actually exists
            log(configName + " could not be found. Making it now!");
            this.saveResource(configName, false); // If it doesn't, create it
            log(configName + " was successfully generated!");
        } else {
            log(configName + " was found!");
        }

        if (customConfigFile.exists()) { // Re-check if the custom config exists
            try {
                Gson gson = new Gson();
                return gson.fromJson(new FileReader(customConfigFile), JsonObject.class);
            } catch (FileNotFoundException e) {
                log(configName + " could not be loaded!");
            }
        } else {
            log(configName + " still doesn't exist!");
        }
        return null;
    }

    /**
     * Reload a config
     * @param name The name of the config to reload
     */
    public void reloadConfig(String name) {
        File config = new File(getDataFolder(), name);
        Gson gson = new Gson();
        try {
            this.configs.put(name,gson.fromJson(new FileReader(config), JsonObject.class));
        } catch (FileNotFoundException e) {
            log(name + " could not be loaded!");
        }
    }

    /**
     * Save a config
     * @param name Name of the config to save to
     * @param data The data to save
     */
    public void saveConfig(String name, Map<String, Object> data) {
        FileConfiguration fileConfiguration;
        File config = new File(getDataFolder(), name);
        fileConfiguration = YamlConfiguration.loadConfiguration(config);
        for(Map.Entry<String, Object> entry : data.entrySet()) {
            fileConfiguration.set(entry.getKey(),entry.getValue());
        }
        try {
            fileConfiguration.save(config);
        } catch (IOException e) {
            log("Failed to save "+name); // shouldn't really happen, but save throws the exception
        }
    }

    /**
     * Sends a String to the log
     * @param log The text to be logged
     */
    public static void log(String log) {
        System.out.println("[ItemCollectibles] " + log);
    }
}
