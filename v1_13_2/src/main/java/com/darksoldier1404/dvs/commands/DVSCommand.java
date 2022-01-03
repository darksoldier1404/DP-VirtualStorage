package com.darksoldier1404.dvs.commands;

import com.darksoldier1404.duc.lang.DLang;
import com.darksoldier1404.dvs.VirtualStorage;
import com.darksoldier1404.dvs.functions.DVSFunction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DVSCommand implements CommandExecutor, TabCompleter {
    private final VirtualStorage plugin = VirtualStorage.getInstance();
    private final String prefix = VirtualStorage.getInstance().prefix;
    private final DLang lang = plugin.lang;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "플레이어만 사용 가능한 명령어 입니다.");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(prefix + lang.get("storage_cmd_select"));
            p.sendMessage(prefix + lang.get("storage_cmd_buy"));
            return false;
        }
        if (args[0].equalsIgnoreCase("선택")) {
            DVSFunction.openStorageSelector(p);
            return true;
        }
        if (args[0].equalsIgnoreCase("구매")) {
            DVSFunction.buyStorage(p);
            return true;
        }
        return true;
    }

    @Override
    public
    List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("선택", "구매");
        }
        return null;
    }
}
