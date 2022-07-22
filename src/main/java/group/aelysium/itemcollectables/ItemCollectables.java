package group.aelysium.itemcollectables;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import group.aelysium.itemcollectables.commands.BagCommand;
import group.aelysium.itemcollectables.commands.ItemCollectablesCommand;
import group.aelysium.itemcollectables.engine.ItemSpawn;
import group.aelysium.itemcollectables.init.ConfigLoader;
import group.aelysium.itemcollectables.init.ModelLoader;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collectible.events.OnItemPickup;
import group.aelysium.itemcollectables.lib.generic.PluginType;
import group.aelysium.itemcollectables.lib.gui.events.OnInventoryClick;
import group.aelysium.itemcollectables.lib.gui.events.OnInventoryDrag;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;

public final class ItemCollectables extends JavaPlugin implements Listener {
    private Integer itemSpawnDelay = 300;
    private PluginType pluginType;
    private final MySQL mySQL = new MySQL();
    private DataSource dataSource;
    public Map<String, JsonObject> configs = new HashMap<>();
    public void setPluginType(PluginType pluginType) { this.pluginType = pluginType; }
    public void setItemSpawnDelay(Integer itemSpawnDelay) { this.itemSpawnDelay = itemSpawnDelay; }

    @Override
    public void onEnable() {
        if(!registerConfigs()) return;

        registerCommands();

        registerEvents();

        ItemSpawn itemSpawn = new ItemSpawn();
        itemSpawn.runTaskTimer(this, 10, this.itemSpawnDelay);

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
        this.getCommand("bag").setExecutor(new BagCommand(this.mySQL));

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
    public boolean registerConfigs() {
        ItemCollectables.log("Initalizing plugin...");

        ConfigLoader.loadRootConfig(this.configs, this, mySQL);

        try {
            mySQL.connect();

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
            return true;
        } catch (ExceptionInInitializerError e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
        return false;
    }

    /**
     * Creates a custom config file using a template from resources/config
     * @param configName The name of the config template to get
     */
    public static JsonObject createCustomConfig(String configName) {
        File customConfigFile = new File(ItemCollectables.getProvidingPlugin(ItemCollectables.class).getDataFolder(), configName); // Load the custom config from the plugins data file
        log("> > " + "Searching for "+configName);
        if (!customConfigFile.exists()) { // Check if the custom config actually exists
            log("> > " + configName + " could not be found. Making it now!");
            ItemCollectables.getProvidingPlugin(ItemCollectables.class).saveResource(configName, false); // If it doesn't, create it
            log("> > " + configName + " was successfully generated!");
        } else {
            log("> > " + configName + " was found!");
        }

        if (customConfigFile.exists()) { // Re-check if the custom config exists
            try {
                Gson gson = new Gson();
                return gson.fromJson(new FileReader(customConfigFile), JsonObject.class);
            } catch (FileNotFoundException e) {
                log("> > " + configName + " could not be loaded!");
            }
        } else {
            log("> > " + configName + " still doesn't exist!");
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
