package com.darksoldier1404.dvs.functions;

import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dvs.VirtualStorage;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class DVSFunction {
    private static final VirtualStorage plugin = VirtualStorage.getInstance();
    private static final DLang lang = plugin.lang;

    public static void buyStorage(Player p) {
        if (plugin.ess == null) {
            p.sendMessage(lang.get("essential_is_not_found"));
            return;
        }
        UUID uuid = p.getUniqueId();
        YamlConfiguration data = plugin.udata.get(uuid);
        if (plugin.config.getInt("Settings.MaxStorage") >= data.getInt("Player.MaxStorage") || data.getInt("Player.MaxStorage") == 54) {
            p.sendMessage(plugin.prefix + lang.getWithArgs("cant_buy_storage_is_max", plugin.config.getInt("Settings.MaxStorage") + ""));
            return;
        }
        final BigDecimal price = new BigDecimal(plugin.getConfig().getString("Settings.Price"));
        if (plugin.ess.getUser(uuid).getMoney().compareTo(price) < 0) {
            p.sendMessage(plugin.prefix + lang.getWithArgs("cant_buy_not_enough_money", price + ""));
            return;
        }
        try {
            plugin.ess.getUser(uuid).setMoney(plugin.ess.getUser(uuid).getMoney().subtract(price));
        } catch (Exception ignored) {
        }
        data.set("Player.MaxStorage", data.getInt("Player.MaxStorage") + 1);
        data.set("Storage." + data.getInt("Player.MaxStorage"), new ItemStack(Material.CHEST));
        p.sendMessage(plugin.prefix + "§a창고 구매 완료!");
        saveData(uuid);
    }

    public static void openStorageSelector(Player p) {
        UUID uuid = p.getUniqueId();
        Inventory inv = plugin.getServer().createInventory(null, 54, "§1창고 선택");
        plugin.udata.get(uuid).getConfigurationSection("Storage").getKeys(false).forEach(key -> inv.setItem(Integer.parseInt(key), plugin.udata.get(uuid).getItemStack("Storage." + key)));
        p.openInventory(inv);
    }

    public static void openStorage(Player p, int num, ItemStack chest) {
        Inventory inv = plugin.getServer().createInventory(null, 54, "§1" + (num+1) + "번 창고");
        try {
            inv.setContents(NBT.getInventoryTag(chest, "dvs_" + num).getContents());
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.openInventory(inv);
    }

    public static void saveStorage(Player p, int num, Inventory inv) {
        UUID uuid = p.getUniqueId();
        YamlConfiguration data = plugin.udata.get(uuid);
        ItemStack chest = new ItemStack(Material.CHEST);
        chest = NBT.setInventoryTag(chest, inv, "dvs_" + num);
        data.set("Storage." + num, chest);
        saveData(uuid);
    }


    public static void initData(UUID uuid) {
        final File file = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
        if (!file.exists()) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            data.set("Storage.0", new ItemStack(Material.CHEST));
            data.set("Player.MaxStorage", 0);
            try {
                data.save(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
                plugin.udata.put(uuid, data);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            YamlConfiguration data = YamlConfiguration
                    .loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            plugin.udata.put(uuid, data);
        }
    }

    public static void saveData(UUID uuid) {
        ConfigUtils.saveCustomData(plugin, plugin.udata.get(uuid), uuid.toString(), "data/");
    }

    public static void quitAndSaveData(UUID uuid) {
        saveData(uuid);
        plugin.udata.remove(uuid);
    }

}
