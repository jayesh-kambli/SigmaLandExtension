package com.sigma.landsextension.commands;

import com.sigma.landsextension.SigmaLandsExtension;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final SigmaLandsExtension plugin;

    public ReloadCommand(SigmaLandsExtension plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sigma.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        try {
            // Reload the configuration
            plugin.reloadConfig();
            
            // Reload zones in ZoneManager
            plugin.getZoneManager().reloadZones();
            
            // Log what was reloaded
            int zoneCount = plugin.getZoneManager().getZoneCount();
            boolean debugEnabled = plugin.getConfig().getBoolean("debug.enabled", false);
            boolean launchPadEnabled = plugin.getConfig().getBoolean("launch-pad.enabled", true);
            double launchPower = plugin.getConfig().getDouble("launch-pad.power", 1.5);
            
            sender.sendMessage(ChatColor.GREEN + "SigmaLandsExtension configuration reloaded successfully!");
            sender.sendMessage(ChatColor.YELLOW + "Loaded " + zoneCount + " zones, Debug: " + debugEnabled + ", Launch Pad: " + launchPadEnabled + " (Power: " + launchPower + ")");
            plugin.getLogger().info("Configuration reloaded by " + sender.getName() + " - " + zoneCount + " zones loaded");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reloading configuration: " + e.getMessage());
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }

        return true;
    }
}
