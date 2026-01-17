package me.kdsoldin.kdsoldinaddon;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownListener implements Listener {

    private final KDSoldinAddon plugin;
    private final Map<UUID, Map<Material, Long>> playerCooldowns = new HashMap<>();

    public CooldownListener(KDSoldinAddon plugin) {
        this.plugin = plugin;
    }

    // ===============================
    // ПРЕДМЕТЫ / ПЕРКИ (КД СРАЗУ)
    // ===============================
    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        Material material = event.getItem().getType();

        // ❗ ЕДУ НЕ ОБРАБАТЫВАЕМ ЗДЕСЬ
        if (material.isEdible()) return;

        if (!plugin.getCooldowns().containsKey(material)) return;

        Player player = event.getPlayer();
        if (player.isOp()) return;

        long currentTime = System.currentTimeMillis();
        long remaining = getRemaining(player, material, currentTime);

        if (remaining > 0) {
            event.setCancelled(true);
            player.sendMessage(plugin.getCooldownMessage(remaining));
            return;
        }

        int cooldown = plugin.getCooldowns().get(material);
        setCooldown(player, material, currentTime, cooldown);
    }

    // ===============================
    // ЕДА (КД ПОСЛЕ ТОГО КАК СЪЕЛ)
    // ===============================
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Material material = event.getItem().getType();
        if (!plugin.getCooldowns().containsKey(material)) return;

        Player player = event.getPlayer();
        if (player.isOp()) return;

        long currentTime = System.currentTimeMillis();
        long remaining = getRemaining(player, material, currentTime);

        // ⛔ если КД есть — не даём есть
        if (remaining > 0) {
            event.setCancelled(true);
            player.sendMessage(plugin.getCooldownMessage(remaining));
            return;
        }

        int cooldown = plugin.getCooldowns().get(material);

        // ✅ ЕДА УЖЕ СЪЕЛАСЬ → СТАВИМ КД
        setCooldown(player, material, currentTime, cooldown);
    }

    // ===============================
    // UTILS
    // ===============================
    private long getRemaining(Player player, Material material, long currentTime) {
        UUID uuid = player.getUniqueId();
        if (!playerCooldowns.containsKey(uuid)) return 0;

        Map<Material, Long> cooldownMap = playerCooldowns.get(uuid);
        if (!cooldownMap.containsKey(material)) return 0;

        long expireTime = cooldownMap.get(material);
        return expireTime > currentTime
                ? (expireTime - currentTime) / 1000
                : 0;
    }

    private void setCooldown(Player player, Material material, long currentTime, int cooldownSeconds) {
        playerCooldowns
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(material, currentTime + cooldownSeconds * 1000L);
    }
}
