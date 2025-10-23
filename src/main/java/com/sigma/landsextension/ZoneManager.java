package com.sigma.landsextension;

import org.bukkit.configuration.ConfigurationSection;
import java.util.*;

public class ZoneManager {
    private final SigmaLandsExtension plugin;
    private final Map<String, List<String>> zonePermissions = new HashMap<>();

    public ZoneManager(SigmaLandsExtension plugin) {
        this.plugin = plugin;
        loadZones();
    }

    public void loadZones() {
        zonePermissions.clear();
        ConfigurationSection areas = plugin.getConfig().getConfigurationSection("restricted-areas");

        if (areas == null) return;

        for (String areaKey : areas.getKeys(false)) {
            ConfigurationSection areaSection = areas.getConfigurationSection(areaKey);
            if (areaSection == null) continue;

            for (String zoneKey : areaSection.getKeys(false)) {
                List<String> perms = areaSection.getStringList(zoneKey);
                zonePermissions.put(zoneKey, perms);
                plugin.getLogger().info("Loaded zone " + zoneKey + " with permissions: " + perms);
            }
        }
    }

    public List<String> getRequiredPermissions(String zoneName) {
        return zonePermissions.getOrDefault(zoneName, Collections.emptyList());
    }

    public void reloadZones() {
        plugin.reloadConfig();
        loadZones();
    }
}
