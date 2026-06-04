package com.sigma.landsextension;

import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.plugin.java.JavaPlugin;

public class SigmaLandsExtension extends JavaPlugin {
    private static SigmaLandsExtension instance;
    private ZoneManager zoneManager;
    private LandsIntegration landsAPI;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (getServer().getPluginManager().getPlugin("Lands") == null) {
            getLogger().severe("[SigmaLandsExtension] Lands plugin not found! Disabling extension.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // ✅ Initialize Lands API
        landsAPI = LandsIntegration.of(this);

        zoneManager = new ZoneManager(this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(zoneManager, landsAPI), this);

        // Register commands
        getCommand("sigmareload").setExecutor(new com.sigma.landsextension.commands.ReloadCommand(this));

        getLogger().info("SigmaLandsExtension enabled successfully with Lands API!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SigmaLandsExtension disabled!");
    }

    public static SigmaLandsExtension getInstance() {
        return instance;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public LandsIntegration getLandsAPI() {
        return landsAPI;
    }
}
