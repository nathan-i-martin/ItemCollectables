package group.aelysium.itemcollectables.init;

import group.aelysium.itemcollectables.ItemCollectables;
import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collectible.models.Bag;
import group.aelysium.itemcollectables.lib.collectible.models.Collectable;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import group.aelysium.itemcollectables.lib.collector.Collector;
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
    public static void loadCollectables(MySQL mySQL) {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement("SELECT * FROM collectables;");
            ResultSet response = request.executeQuery();

            while (response.next()) {
                String name =           response.getString("name");
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
                if(family == null) throw new NullPointerException();

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
            ItemCollectables.log("Unable to load collectables!");
        }
    }

    public static void loadFamilies(MySQL mySQL) {
        try {
            Connection conn = mySQL.getConnection();

            PreparedStatement request = conn.prepareStatement("SELECT * FROM families;");
            ResultSet response = request.executeQuery();

            while (response.next()) {
                String name =  response.getString("name");
                Integer guiRows = response.getInt("gui_rows");

                Family family = new Family(
                        name,
                        guiRows
                );

                Family.register(family);
            }
            ItemCollectables.log("Loaded families successfully!");
        } catch (SQLException e) {
            ItemCollectables.log("Unable to load families!");
        }
    }

    public static void loadCollectors(MySQL mySQL) {
        List<? extends Player> players = Bukkit.getOnlinePlayers().stream().toList();

        Connection connection = mySQL.getConnection();

        players.forEach(player -> {
            try {
                PreparedStatement request = connection.prepareStatement(
                        "SELECT * FROM player_collectables " +
                                "WHERE uuid = ? " +
                                "OUTER JOIN collectables " +
                                "ON player_collectables.collectable_name=collectables.name;"
                );
                request.setString(0,player.getUniqueId().toString());
                ResultSet response = request.executeQuery();

                if(response.getFetchSize() == 0) return;

                while (response.next()) {
                    UUID uuid = UUID.fromString(response.getString("uuid"));

                    Collector collector;
                    if(Collector.contains(uuid)) {
                        collector = Collector.find(uuid);
                    } else {
                        collector = new Collector(uuid);
                        Collector.add(collector);
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
            } catch (SQLException e) {
                ItemCollectables.log("Unable to load collectors!");
            }
        });
        ItemCollectables.log("Loaded collectors successfully!");
    }
}
