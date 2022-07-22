package group.aelysium.itemcollectables.commands;

import group.aelysium.itemcollectables.lib.MySQL;
import group.aelysium.itemcollectables.lib.collectible.models.Family;
import group.aelysium.itemcollectables.lib.collector.models.Collector;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BagCommand implements CommandExecutor {
    private MySQL mySQL;

    public BagCommand(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    private final String chatPrefix = ChatColor.DARK_GRAY+"["+ChatColor.AQUA+"ItemCollectables"+ChatColor.DARK_GRAY+"]: "+ChatColor.GRAY;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {} else {
            sender.sendMessage(chatPrefix + ChatColor.RED + "You must send this command as a player!");
            return true;
        }
        Player player = (Player) sender;

        Collector.getReliably(player.getUniqueId(), mySQL);

        Family.openFamilySelectorGUI(player);

        return true;
    }

}
