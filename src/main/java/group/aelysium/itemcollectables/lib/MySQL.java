package group.aelysium.itemcollectables.lib;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import group.aelysium.itemcollectables.ItemCollectables;
import org.bukkit.Bukkit;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MySQL {
    private DataSource dataSource;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    public MySQL(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }


    /**
     * Tests the connection to the provided MySQL server
     */
    public void connect() {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
                        dataSource.setServerName(this.host);
                        dataSource.setPortNumber(this.port);
                        dataSource.setDatabaseName(this.database);
                        dataSource.setUser(this.user);
                        dataSource.setPassword(this.password);

        try {
            Connection connection = dataSource.getConnection();
            if (connection.isValid(1000)) {
                this.dataSource = dataSource;
            } else {
                ItemCollectables.log("> > Unable to connect to the database!");
                Bukkit.getPluginManager().disablePlugin(ItemCollectables.getProvidingPlugin(ItemCollectables.class));
            }
        } catch (SQLException e) {
            ItemCollectables.log("> > Unable to connect to the database!");
            Bukkit.getPluginManager().disablePlugin(ItemCollectables.getProvidingPlugin(ItemCollectables.class));
        }
    }

    /**
     * Get's the connection
     */
    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            ItemCollectables.log("There was an issue while getting the database connection!");
        }
        return null;
    }



    /**
     * Initializes the database
     */
    public void init(ItemCollectables itemCollectables) {
        ItemCollectables.log("> Beginning database setup...");

        List<String> scripts = new ArrayList<String>(4);
                     scripts.add(0,"drop_tables");
                     scripts.add(1,"families");
                     scripts.add(2,"collectables");
                     scripts.add(3,"player_collectables");

        scripts.forEach(script -> {
            try {
                ItemCollectables.log("> > Preparing database setup request...");
                InputStream stream = ItemCollectables.getResourceAsStream("dbsetup/"+script+".sql",itemCollectables);
                String setup = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));

                String[] queries = setup.split(";");

                ItemCollectables.log("> > Issuing database setup request...");
                for (String query : queries) {
                    if (query.isEmpty()) continue;
                    try (Connection conn = this.getConnection();
                         PreparedStatement request = conn.prepareStatement(query)) {
                        request.execute();
                    }
                }
                ItemCollectables.log("> > Database setup complete!");
            } catch (Exception e) {
                ItemCollectables.log("Could not init the database!");
                e.printStackTrace();
            }
        });
        return;
    }
}
