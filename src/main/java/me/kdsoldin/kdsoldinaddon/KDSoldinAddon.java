package me.kdsoldin.kdsoldinaddon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class KDSoldinAddon extends JavaPlugin {

    private static KDSoldinAddon instance;
    private CooldownListener cooldownListener;
    private Map<Material, Integer> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadCooldowns();

        cooldownListener = new CooldownListener(this);
        Bukkit.getPluginManager().registerEvents(cooldownListener, this);

        getCommand("kdsreload").setExecutor(new ReloadCommand(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(cooldownListener);
    }

    public void reload() {
        reloadConfig();
        loadCooldowns();
    }

    private void loadCooldowns() {
        cooldowns.clear();
        FileConfiguration config = getConfig();
        if (config.isConfigurationSection("cooldowns")) {
            for (String key : config.getConfigurationSection("cooldowns").getKeys(false)) {
                try {
                    Material material = Material.valueOf(key);
                    int seconds = config.getInt("cooldowns." + key);
                    cooldowns.put(material, seconds);
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Неверный материал в конфиге: " + key);
                }
            }
        }
    }

    public String getCooldownMessage(long timeLeft) {
        String msg = getConfig().getString("messages.cooldown", "&cИспользовать этот предмет можно будет через %time% сек.");
        msg = msg.replace("%time%", String.valueOf(timeLeft));
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public Map<Material, Integer> getCooldowns() {
        return cooldowns;
    }

    public static KDSoldinAddon getInstance() {
        return instance;
    }
}
