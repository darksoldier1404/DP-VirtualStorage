package com.darksoldier1404.dvs.functions;

import com.darksoldier1404.duc.lang.DLang;
import com.darksoldier1404.duc.utils.ConfigUtils;
import com.darksoldier1404.dvs.VirtualStorage;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

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
        data.set("Storage." + (data.getInt("Player.MaxStorage") + 1), new ItemStack(Material.BUNDLE));
        p.sendMessage(plugin.prefix + "§a창고 구매 완료!");
        saveData(uuid);
    }

    public static void openStorageSelector(Player p) {
        UUID uuid = p.getUniqueId();
        Inventory inv = plugin.getServer().createInventory(null, 54, "§1창고 선택");
        plugin.udata.get(uuid).getConfigurationSection("Storage").getKeys(false).forEach(key -> inv.setItem(Integer.parseInt(key) - 1, plugin.udata.get(uuid).getItemStack("Storage." + key)));
        p.openInventory(inv);
    }

    public static void openStorage(Player p, int num, ItemStack bundle) {
        Inventory inv = plugin.getServer().createInventory(null, 54, "§1" + num + "번 창고");
        BundleMeta bm = (BundleMeta) bundle.getItemMeta();
        bm.getItems().forEach(inv::addItem);
        p.openInventory(inv);
    }

    public static void saveStorage(Player p, int num, Inventory inv) {
        UUID uuid = p.getUniqueId();
        YamlConfiguration data = plugin.udata.get(uuid);
        ItemStack bundle = new ItemStack(Material.BUNDLE);
        BundleMeta bm = (BundleMeta) bundle.getItemMeta();
        Arrays.stream(inv.getContents()).collect(Collectors.toSet()).forEach(item -> {
            if (item != null && item.getType() != Material.AIR) {
                bm.addItem(item);
            }
        });
        bundle.setItemMeta(bm);
        data.set("Storage." + num, bundle);
        saveData(uuid);
    }


    public static void initData(UUID uuid) {
        final File file = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
        if (!file.exists()) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            data.set("Storage.1", new ItemStack(Material.BUNDLE));
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
