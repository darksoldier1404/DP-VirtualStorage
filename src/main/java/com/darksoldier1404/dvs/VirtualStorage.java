package com.darksoldier1404.dvs;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dvs.commands.DVSCommand;
import com.darksoldier1404.dvs.events.DVSEvent;
import com.darksoldier1404.dvs.functions.DVSFunction;
import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class VirtualStorage extends JavaPlugin {
    private DPPCore core;
    private static VirtualStorage plugin;
    public String prefix;
    public YamlConfiguration config;
    public Map<UUID, YamlConfiguration> udata = new HashMap<>();
    public Map<UUID, Integer> currentInventory = new HashMap<>();
    public Map<String, YamlConfiguration> langFiles = new HashMap<>();
    public Essentials ess = null;
    public DLang lang;

    public static VirtualStorage getInstance() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        Plugin pl = getServer().getPluginManager().getPlugin("DPP-Core");
        if (pl == null) {
            getLogger().warning("DPP-Core 플러그인이 설치되어있지 않습니다.");
            plugin.setEnabled(false);
            return;
        }
        core = (DPPCore) pl;
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.prefix"));
        lang = new DLang(config.getString("Lang") == null ? "Korean" : config.getString("Lang"), plugin);
        plugin.getServer().getPluginManager().registerEvents(new DVSEvent(), plugin);
        getCommand("창고").setExecutor(new DVSCommand());
    }

    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
        udata.keySet().forEach(DVSFunction::saveData);
    }
}
