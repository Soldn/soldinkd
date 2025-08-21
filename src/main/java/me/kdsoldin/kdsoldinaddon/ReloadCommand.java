package me.kdsoldin.kdsoldinaddon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class ReloadCommand implements CommandExecutor {

    private final KDSoldinAddon plugin;

    public ReloadCommand(KDSoldinAddon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kds.reload")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав на выполнение этой команды.");
            return true;
        }

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Конфиг успешно перезагружен!");
        return true;
    }
}
