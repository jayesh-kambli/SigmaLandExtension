package com.sigma.landsextension;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.events.player.area.PlayerAreaEnterEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PlayerMoveListener implements Listener {
    private final ZoneManager zoneManager;
    private final LandsIntegration landsAPI;

    public PlayerMoveListener(ZoneManager zoneManager, LandsIntegration landsAPI) {
        this.zoneManager = zoneManager;
        this.landsAPI = landsAPI;
    }

    @EventHandler
    public void onEnterArea(PlayerAreaEnterEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerUUID());
        if (player == null) return;

        Area area = event.getArea();
        Land land = area.getLand();
        String landName = land.getName();
        String areaName = area.getName();

        // Fetch required permissions for this specific area
        List<String> requiredPermissions = zoneManager.getRequiredPermissions(areaName);
        if (requiredPermissions.isEmpty()) return; // No restrictions for this area

        // Check if player has all required permissions
        boolean hasAllPermissions = requiredPermissions.stream().allMatch(player::hasPermission);

        if (hasAllPermissions) {
            // Player has all required permissions
            if (!area.isTrusted(player.getUniqueId())) {
                area.trustPlayer(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "You've been granted access to " + areaName + ".");
            }
        } else {
            // ❌ Player missing permission → deny entry and push out
            event.setCancelled(true);

            // Untrust if previously trusted (handle owner exceptions)
            if (area.isTrusted(player.getUniqueId())) {
                try {
                    area.untrustPlayer(player.getUniqueId());
                } catch (Exception e) {
                    // Silently handle untrust exceptions (e.g., owner can't be untrusted)
                    if (SigmaLandsExtension.getInstance().getConfig().getBoolean("debug.enabled", false)) {
                        SigmaLandsExtension.getInstance().getLogger().info("Could not untrust player " + player.getName() + " (likely owner): " + e.getMessage());
                    }
                }
            }

            // Send warning message
            player.sendMessage(ChatColor.RED + "You don't have access to this area (" + areaName + ").");

            // Schedule launch pad effect on main thread to avoid async issues
            player.getScheduler().run(SigmaLandsExtension.getInstance(), task -> {
                // Check if launch pad is enabled
                if (!SigmaLandsExtension.getInstance().getConfig().getBoolean("launch-pad.enabled", true)) {
                    // Fallback to teleportation if launch pad is disabled
                    if (event.getFrom() != null && event.getFrom().getSpawn() != null) {
                        player.teleportAsync(event.getFrom().getSpawn().toLocation());
                    } else {
                        player.teleportAsync(player.getWorld().getSpawnLocation());
                    }
                    return;
                }
                
                // Calculate direction away from the restricted area
                org.bukkit.Location playerLoc = player.getLocation();
                
                // Use a simple approach: push player away from their current position
                // This creates a "bounce back" effect
                org.bukkit.util.Vector direction = new org.bukkit.util.Vector(
                    -Math.signum(playerLoc.getX()), // Push opposite to X direction
                    0.5, // Always push up a bit
                    -Math.signum(playerLoc.getZ())  // Push opposite to Z direction
                ).normalize();
                
                // Apply launch pad effect with configurable power
                double launchPower = SigmaLandsExtension.getInstance().getConfig().getDouble("launch-pad.power", 1.5);
                direction.multiply(launchPower);
                
                // Add upward velocity for that "launch" effect
                direction.setY(Math.abs(direction.getY()) + 0.5);
                
                // Apply the velocity to push player away
                player.setVelocity(direction);
                
                // Add visual/audio effects if enabled
                if (SigmaLandsExtension.getInstance().getConfig().getBoolean("launch-pad.sound", true)) {
                    player.getWorld().playSound(playerLoc, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                }
                
                if (SigmaLandsExtension.getInstance().getConfig().getBoolean("launch-pad.particles", true)) {
                    player.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, playerLoc, 20, 0.5, 0.5, 0.5, 0.1);
                }
            }, null);

            // Debug Log (only if debug is enabled)
            if (SigmaLandsExtension.getInstance().getConfig().getBoolean("debug.enabled", false)) {
                SigmaLandsExtension.getInstance().getLogger().info("[SigmaLandsExtension] Player " + player.getName()
                        + " denied access to " + areaName + " (missing permission). Launched away.");
            }
        }
    }
}
