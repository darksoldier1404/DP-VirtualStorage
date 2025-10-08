package com.darksoldier1404.dpvs.events;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dpvs.functions.DPVSFunction;
import com.darksoldier1404.dpvs.obj.VUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.darksoldier1404.dpvs.VirtualStorage.plugin;

public class DPVSEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!plugin.udata.containsKey(p.getUniqueId())) {
            VUser data = new VUser();
            data.setUUID(p.getUniqueId());
            DInventory inv = new DInventory("Storage", 54, true, true, plugin);
            inv.update();
            data.setInventory(inv);
            plugin.udata.put(p.getUniqueId(), data);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        plugin.udata.save(p.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getClickedInventory().getHolder() == null) {
            return;
        }
        if (e.getClickedInventory().getHolder() instanceof DInventory) {
            DInventory inv = (DInventory) e.getClickedInventory().getHolder();
            Player p = (Player) e.getWhoClicked();
            if (inv.isValidHandler(plugin)) {
                ItemStack item = e.getCurrentItem();
                if (item == null || item.getType().isAir()) {
                    return;
                }
                if (NBT.hasTagKey(item, "dppc_prevpage")) {
                    inv.applyChanges();
                    inv.prevPage();
                    e.setCancelled(true);
                    return;
                }
                if (NBT.hasTagKey(item, "dppc_nextpage")) {
                    inv.applyChanges();
                    inv.nextPage();
                    e.setCancelled(true);
                    return;
                }
                if (NBT.hasTagKey(item, "dppc_clickcancel") || NBT.hasTagKey(item, "dpvs_barrier")) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() == null) return;
        if (e.getInventory().getHolder() instanceof DInventory) {
            Player p = (Player) e.getPlayer();
            DInventory inv = (DInventory) e.getInventory().getHolder();
            if (inv.isValidHandler(plugin)) {
                if (inv.isValidChannel(0)) { // user storage save
                    inv.applyChanges();
                    VUser user = plugin.udata.get(p.getUniqueId());
                    user.setInventory(inv);
                    plugin.udata.put(p.getUniqueId(), user);
                    plugin.udata.save(p.getUniqueId());
                    return;
                }
                if (inv.isValidChannel(1)) { // admin lookup save
                    inv.applyChanges();
                    if (inv.getObj() != null) {
                        VUser user = plugin.udata.get((UUID) inv.getObj());
                        user.setInventory(inv);
                        plugin.udata.put((UUID) inv.getObj(), user);
                        plugin.udata.save((UUID) inv.getObj());
                    }
                    return;
                }
                if (inv.isValidChannel(101)) {
                    inv.applyChanges();
                    DPVSFunction.saveCouponItem(inv.getItem(13));
                    p.sendMessage(plugin.getPrefix() + "§a쿠폰 아이템이 저장되었습니다.");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        ItemStack item = e.getItem();
        if (item == null || item.getType().isAir()) return;
        Player p = e.getPlayer();
        if (NBT.hasTagKey(item, "dpvs_couponslot")) {
            e.setCancelled(true);
            int slots = NBT.getIntegerTag(item, "dpvs_couponslot");
            if (slots < 1) {
                p.sendMessage("§c쿠폰 슬롯이 올바르지 않습니다.");
                return;
            }
            if (plugin.udata.get(p.getUniqueId()).addStorageSlots(slots)) {
                p.sendMessage("§a쿠폰이 사용되었습니다. 가상 창고 슬롯이 " + slots + "칸 증가하였습니다.");
                item.setAmount(item.getAmount() - 1);
            } else {
                p.sendMessage("§c가상 창고 슬롯을 더 이상 늘릴 수 없습니다.");
            }
        }
    }
}
