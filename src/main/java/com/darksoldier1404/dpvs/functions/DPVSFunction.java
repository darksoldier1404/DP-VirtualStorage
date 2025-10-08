package com.darksoldier1404.dpvs.functions;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import static com.darksoldier1404.dpvs.VirtualStorage.plugin;

public class DPVSFunction {
    public static void setDefaultStorageSlot(CommandSender sender, int slot) {
        plugin.defaultStorageSlot = slot;
        plugin.getConfig().set("Settings.defaultStorageSlot", slot);
        plugin.saveDataContainer();
        sender.sendMessage("§a기본 저장소 슬롯이 " + slot + "으로 설정되었습니다.");
    }

    @Nullable
    public static OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer op : plugin.getServer().getOfflinePlayers()) {
            if (op.getName() != null && op.getName().equalsIgnoreCase(name)) {
                return op;
            }
        }
        return null;
    }

    public static void openCouponSettingGUI(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c플레이어만 사용 가능한 명령어입니다.");
            return;
        }
        Player p = (Player) sender;
        DInventory inv = new DInventory("§6쿠폰 설정", 27, plugin);
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pane.setItemMeta(meta);
        pane = NBT.setStringTag(pane, "dppc_clickcancel", "true");
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, pane);
        }
        inv.setItem(13, plugin.config.getItemStack("Settings.couponItem"));
        inv.setChannel(101);
        inv.openInventory(p);
    }

    public static void saveCouponItem(ItemStack item) {
        plugin.getConfig().set("Settings.couponItem", item);
        plugin.saveDataContainer();
    }

    public static ItemStack getCoupon(int slots) {
        ItemStack coupon = plugin.config.getItemStack("Settings.couponItem").clone();
        coupon = NBT.setIntTag(coupon, "dpvs_couponslot", slots);
        return coupon;
    }

    public static void giveCoupon(CommandSender sender, String sSlots) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c플레이어만 사용 가능한 명령어입니다.");
            return;
        }
        Player p = (Player) sender;
        int slots;
        try {
            slots = Integer.parseInt(sSlots);
            if (slots < 1) {
                p.sendMessage("§c슬롯은 1 이상이어야 합니다.");
                return;
            }
        } catch (NumberFormatException e) {
            p.sendMessage("§c숫자 형식이 올바르지 않습니다.");
            return;
        }
        ItemStack coupon = getCoupon(slots);
        coupon = applyPlaceholder(coupon);
        p.getInventory().addItem(coupon);
        p.sendMessage("§a쿠폰을 지급하였습니다. (" + slots + " 슬롯)");
    }

    public static ItemStack applyPlaceholder(ItemStack item) {
        if (item == null || item.getType().isAir()) return item;
        if (NBT.hasTagKey(item, "dpvs_couponslot")) {
            int slots = NBT.getIntegerTag(item, "dpvs_couponslot");
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String name = meta.getDisplayName();
                name = name.replace("%slots%", String.valueOf(slots));
                meta.setDisplayName(name);
                if (meta.hasLore()) {
                    for (int i = 0; i < meta.getLore().size(); i++) {
                        String lore = meta.getLore().get(i);
                        lore = lore.replace("%slots%", String.valueOf(slots));
                        meta.getLore().set(i, lore);
                    }
                }
                item.setItemMeta(meta);
            }
        }
        return item;
    }
}
