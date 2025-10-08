package com.darksoldier1404.dpvs.commands;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import com.darksoldier1404.dpvs.functions.DPVSFunction;
import com.darksoldier1404.dpvs.obj.VUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

import static com.darksoldier1404.dpvs.VirtualStorage.plugin;

public class DPVSCommand {
    private final CommandBuilder builder;

    public DPVSCommand() {
        builder = new CommandBuilder(plugin);
        builder.addSubCommand("defaultslot", "dpvs.admin", "/dpvs defaultslot <slot>", false, (p, args) -> {
            if (args.length == 2) {
                try {
                    int slot = Integer.parseInt(args[1]);
                    if (slot < 1) {
                        p.sendMessage(plugin.getPrefix() + "§c슬롯은 1 이상이어야 합니다.");
                        return true;
                    }
                    plugin.defaultStorageSlot = slot;
                    plugin.getConfig().set("Settings.defaultStorageSlot", slot);
                    plugin.saveDataContainer();
                    p.sendMessage(plugin.getPrefix() + "§a기본 저장소 슬롯이 " + slot + "으로 설정되었습니다.");
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        });

        builder.addSubCommand("open", "dpvs.open", "/dpvs open", true, (p, args) -> {
            if (!(p instanceof Player)) {
                p.sendMessage(plugin.getPrefix() + "§c플레이어만 사용 가능한 명령어입니다.");
                return true;
            }
            if (args.length == 1) {
                Player player = (Player) p;
                VUser user = plugin.udata.get(player.getUniqueId());
                user.openInventory(player, false);
                return true;
            }
            return false;
        });

        builder.addSubCommand("lookup", "dpvs.admin", "/dpvs lookup <player>", true, (p, args) -> {
            if (!(p instanceof Player)) {
                p.sendMessage(plugin.getPrefix() + "§c플레이어만 사용 가능한 명령어입니다.");
                return true;
            }
            if (args.length == 2) {
                OfflinePlayer target = DPVSFunction.getOfflinePlayer(args[1]);
                if (target == null) {
                    p.sendMessage(plugin.getPrefix() + "§c플레이어를 찾을 수 없습니다.");
                    return true;
                }
                Player player = (Player) p;
                VUser user = plugin.udata.get(target.getUniqueId());
                if (user == null) {
                    p.sendMessage(plugin.getPrefix() + "§c해당 플레이어의 데이터가 존재하지 않습니다.");
                    return true;
                }
                user.openInventory(player, true);
                return true;
            }
            return false;
        });

        // set coupon item
        builder.addSubCommand("setcoupon", "dpvs.admin", "/dpvs setcoupon", true, (p, args) -> {
            if (args.length == 1) {
                if (!(p instanceof Player)) {
                    p.sendMessage(plugin.getPrefix() + "§c플레이어만 사용 가능한 명령어입니다.");
                    return true;
                }
                Player player = (Player) p;
                DPVSFunction.openCouponSettingGUI(player);
                return true;
            }
            return false;
        });

        // give coupon
        builder.addSubCommand("givecoupon", "dpvs.admin", "/dpvs givecoupon <slots>", true, (p, args) -> {
            if (args.length == 2) {
                DPVSFunction.giveCoupon(p, args[1]);
                return true;
            }
            return false;
        });

        builder.addSubCommand("reload", "dpvs.admin", "/dpvs reload", false, (p, args) -> {
            if (args.length == 1) {
                plugin.reload();
                p.sendMessage(plugin.getPrefix() + "§a플러그인이 리로드되었습니다.");
                return true;
            }
            return false;
        });

        for (String c : builder.getSubCommandNames()) {
            builder.addTabCompletion(c, (sender, args) -> {
                if (c.equals("lookup")) {
                    if (args.length == 2) {
                        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                    }
                }
                return null;
            });
        }
    }

    public CommandBuilder getBuilder() {
        return builder;
    }
}
