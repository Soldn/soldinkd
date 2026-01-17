package me.kdsoldin.kdsoldinaddon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final KDSoldinAddon plugin;

    public ReloadCommand(KDSoldinAddon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("kdsoldin.reload")) {
            sender.sendMessage("§cУ тебя нет прав на выполнение этой команды!");
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage("§aКонфиг плагина успешно перезагружен!");

        return true;
    }
}
