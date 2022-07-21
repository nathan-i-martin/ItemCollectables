package group.aelysium.itemcollectibles.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemCollectablesCommand implements CommandExecutor {

    public ItemCollectablesCommand() {}

    private final String chatPrefix = ChatColor.DARK_GRAY+"["+ChatColor.AQUA+"ItemCollectables"+ChatColor.DARK_GRAY+"]: "+ChatColor.GRAY;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*if (sender instanceof Player) {} else {
            sender.sendMessage(chatPrefix + ChatColor.RED + "You must send this command as a player!");
            return true;
        }
        Player player = (Player) sender;*/
        try {
            if(args[0] == "bag") {

            }
        } catch (Exception e) {
            sender.sendMessage(chatPrefix + ChatColor.RED + "Proper usage: " + ChatColor.GOLD + "/ItemCollectables <universe name>");
        }
        return true;
    }

}
