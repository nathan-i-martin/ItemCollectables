package group.aelysium.itemcollectables.init;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collectible.models.Bag;
import group.aelysium.itemcollectables.lib.collectible.models.Collectable;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import group.aelysium.itemcollectables.lib.collector.models.Collector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ModelLoader {
    public static void loadCollectables(MySQL mySQL) throws ExceptionInInitializerError {
        ItemCollectables.log("Loading collectables from database...");

        try {
            ItemCollectables.log("> Making database request...");
            Connection connection = mySQL.getConnection();

            PreparedStatement request = connection.prepareStatement("SELECT * FROM collectables;");
            ResultSet response = request.executeQuery();

            ItemCollectables.log("> > Processing database response...");

            while (response.next()) {
                String name =           response.getString("collectable_name");
                String lore =           response.getString("lore");
                Integer guiSlotIndex =  response.getInt("gui_slot_index");
                String material =       response.getString("material");
                Location location = new Location(
                        Bukkit.getWorld(response.getString("world")),
                        response.getDouble("x"),
                        response.getDouble("y"),
                        response.getDouble("z")
                );
                Double activeRadius =   response.getDouble("active_radius");
                boolean isEnchanted =   response.getBoolean("enchanted");
                boolean isGlowing =     response.getBoolean("glowing");
                Integer customModelData = response.getInt("custom_model_data");
                String familyName =     response.getString("family_name");

                Family family = Family.find(familyName);
                if(family == null) {
                    ItemCollectables.log("You have collectables that are referencing families that don't exist!");
                    throw new NullPointerException();
                }

                Collectable collectable = new Collectable(
                        name,
                        lore,
                        Material.getMaterial(material),
                        location,
                        activeRadius,
                        isGlowing,
                        isEnchanted,
                        guiSlotIndex,
                        customModelData
                );

                family.addCollectable(collectable);
            }
            ItemCollectables.log("Loaded collectables successfully!");
        } catch (SQLException e) {
            throw new ExceptionInInitializerError();
        }
    }

    public static void loadFamilies(MySQL mySQL) throws ExceptionInInitializerError {
        ItemCollectables.log("Loading families from database...");
        try {
            ItemCollectables.log("> Making database request...");
            Connection connection = mySQL.getConnection();

            PreparedStatement request = connection.prepareStatement("SELECT * FROM families;");
            ResultSet response = request.executeQuery();

            ItemCollectables.log("> > Processing database response...");
            while (response.next()) {
                String name = response.getString("family_name");
                Integer guiRows = response.getInt("gui_rows");
                Material familyItemMaterial = Material.getMaterial(response.getString("family_icon_material"));
                Integer familyItemCMD = response.getInt("family_icon_CMD");
                Material missingGUIItemMaterial = Material.getMaterial(response.getString("gui_missing_item_material"));
                Integer missingGUIItemCMD = response.getInt("gui_missing_item_CMD");

                Family family = new Family(
                        name,
                        guiRows,
                        familyItemMaterial,
                        familyItemCMD,
                        missingGUIItemMaterial,
                        missingGUIItemCMD
                );

                Family.register(family);
            }
            ItemCollectables.log("Loaded families successfully!");
        } catch (SQLException e) {
            throw new ExceptionInInitializerError();
        }
    }

    public static void loadCollectors(MySQL mySQL) throws ExceptionInInitializerError {
        ItemCollectables.log("Loading collectors from database...");
        List<? extends Player> players = Bukkit.getOnlinePlayers().stream().toList();

        Connection connection = mySQL.getConnection();

        ItemCollectables.log("> Making database request...");
        players.forEach(player -> {
            try {
                PreparedStatement request = connection.prepareStatement(
                        "SELECT * FROM player_collectables " +
                                "INNER JOIN collectables " +
                                "USING(collectable_name) " +
                                "WHERE uuid = ?;"
                );
                request.setString(1,player.getUniqueId().toString());
                ResultSet response = request.executeQuery();

                if(!response.isBeforeFirst()) return;

                ItemCollectables.log("> > Processing database response...");

                while (response.next()) {
                    UUID uuid = UUID.fromString(response.getString("uuid"));

                    Collector collector;
                    if(Collector.contains(uuid)) {
                        collector = Collector.find(uuid);
                    } else {
                        collector = new Collector(uuid);
                        Collector.register(collector);
                    }

                    String collectableName = response.getString("collectable_name");
                    String familyName = response.getString("family_name");

                    Family family = Family.find(familyName);
                    if(family == null) throw new NullPointerException();

                    Bag bag;
                    if(collector.hasBag(family)) {
                        bag = collector.findBag(family);
                    } else {
                        bag = collector.holdBag(
                                new Bag(
                                    family
                                )
                        );
                    }

                    bag.add(collectableName);
                }

                ItemCollectables.log("Loaded collectors successfully!");
            } catch (SQLException e) {
                throw new ExceptionInInitializerError();
            }
        });
    }
}
