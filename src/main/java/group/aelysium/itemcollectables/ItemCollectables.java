package group.aelysium.itemcollectibles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import group.aelysium.itemcollectibles.commands.ItemCollectablesCommand;
import group.aelysium.itemcollectibles.init.ConfigLoader;
import group.aelysium.itemcollectibles.init.ModelLoader;
import group.aelysium.itemcollectibles.lib.MySQL;
import group.aelysium.itemcollectibles.lib.collectible.events.OnItemPickup;
import group.aelysium.itemcollectibles.lib.collectible.models.Collectable;
import group.aelysium.itemcollectibles.lib.generic.PluginType;
import group.aelysium.itemcollectibles.lib.gui.events.OnInventoryClick;
import group.aelysium.itemcollectibles.lib.gui.events.OnInventoryDrag;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class ItemCollectables extends JavaPlugin implements Listener {
    private PluginType pluginType;
    private MySQL mySQL;
    private DataSource dataSource;
    public Map<String, JsonObject> configs = new HashMap<>();

    public void setMySQL(MySQL mySQL) { this.mySQL = mySQL; }
    public void setPluginType(PluginType pluginType) { this.pluginType = pluginType; }

    @Override
    public void onEnable() {
        registerConfigs();

        mySQL.connect();

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
        this.getCommand("ItemCollectables").setExecutor(new ItemCollectablesCommand());

    }

    /**
     * Register all of the plugin's Event Listeners
     */
    public void registerEvents() {
        PluginManager eventManager = getServer().getPluginManager();

        eventManager.registerEvents(new OnInventoryDrag(this), this);
        eventManager.registerEvents(new OnInventoryClick(this), this);

        eventManager.registerEvents(new OnItemPickup(this, mySQL), this);
    }

    /**
     * Register the plugin's config files
     */
    public void registerConfigs() {
        ItemCollectables.log("Registering configs...");

        ConfigLoader.loadRootConfig(this.configs, this);

        if(this.pluginType == PluginType.ROOT) {
            mySQL.init(this);

            ConfigLoader.saveFamilies(this.configs, mySQL);
            ConfigLoader.saveCollectables(this.configs,mySQL);

            ModelLoader.loadFamilies(mySQL);
            ModelLoader.loadCollectables(mySQL);

            ModelLoader.loadCollectors(mySQL);

            ConfigLoader.loadGUIs();
        }
        if(this.pluginType == PluginType.BRANCH) {
            ModelLoader.loadFamilies(mySQL);
            ModelLoader.loadCollectables(mySQL);

            ModelLoader.loadCollectors(mySQL);

            ConfigLoader.loadGUIs();
        }

        ItemCollectables.log("Finished registering configs!");
    }

    /**
     * Creates a custom config file using a template from resources/config
     * @param configName The name of the config template to get
     */
    public static JsonObject createCustomConfig(String configName) {
        File customConfigFile = new File(ItemCollectables.getProvidingPlugin(ItemCollectables.class).getDataFolder(), configName); // Load the custom config from the plugins data file
        log("Searching for "+configName);
        if (!customConfigFile.exists()) { // Check if the custom config actually exists
            log(configName + " could not be found. Making it now!");
            ItemCollectables.getProvidingPlugin(ItemCollectables.class).saveResource(configName, false); // If it doesn't, create it
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

    public static InputStream getResourceAsStream(String filename, ItemCollectables itemCollectables) {
        return itemCollectables.getClassLoader().getResourceAsStream(filename);
    }
}
