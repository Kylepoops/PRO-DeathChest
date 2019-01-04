package sk.drawethree.deathchestpro.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sk.drawethree.deathchestpro.DeathChestPro;
import sk.drawethree.deathchestpro.managers.DeathChestManager;
import sk.drawethree.deathchestpro.utils.Message;

public class DeathChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        if (cmd.getName().equalsIgnoreCase("deathchest")) {
            if (args.length > 0) {
                String subCommand = args[0].toLowerCase();
                switch (subCommand) {
                    case "reload":
                        return reloadSubCommand(sender);
                    case "list":
                        return listSubCommand(sender);
                }
            } else {
                return listSubCommand(sender);
            }
        }
        return false;
    }

    private boolean listSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("deathchestpro.list")) {
                DeathChestManager.getInstance().openDeathchestList(p, 1);
                return true;
            }
        }
        return false;
    }

    private boolean reloadSubCommand(CommandSender sender) {
        if (sender.hasPermission("deathchestpro.reload")) {
            DeathChestPro.getInstance().reloadPlugin();
            sender.sendMessage(Message.PREFIX.getMessage() + "Plugin reloaded !");
            return true;
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
