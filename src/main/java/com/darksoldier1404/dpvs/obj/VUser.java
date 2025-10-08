package com.darksoldier1404.dpvs.obj;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DataCargo;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;

import static com.darksoldier1404.dpvs.VirtualStorage.plugin;

public class VUser implements DataCargo {
    private UUID uuid;
    private int maxStorageSlot;
    private DInventory inventory;

    public VUser() {
    }

    public VUser(UUID uuid, int maxStorageSlot, DInventory inventory) {
        this.uuid = uuid;
        this.maxStorageSlot = maxStorageSlot;
        this.inventory = inventory;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public int getMaxStorageSlot() {
        return maxStorageSlot;
    }

    public void setMaxStorageSlot(int maxStorageSlot) {
        this.maxStorageSlot = maxStorageSlot;
    }

    public DInventory getInventory() {
        return inventory;
    }

    public void setInventory(DInventory inventory) {
        this.inventory = inventory;
    }

    public void openInventory(Player p, boolean isLookup) {
        int availableSlots = maxStorageSlot + plugin.defaultStorageSlot;
        int pages = (int) Math.ceil((double) availableSlots / 45.0);
        inventory.setPages(pages - 1);
        inventory.setCurrentPage(0);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta im = barrier.getItemMeta();
        im.setDisplayName("§c잠김");
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        barrier.setItemMeta(im);
        barrier = NBT.setStringTag(barrier, "dpvs_barrier", "true");

        inventory.applyAllItemChanges((pi) -> {
            if (NBT.hasTagKey(pi.getItem(), "dpvs_barrier")) {
                inventory.setPageItem(pi.getPage(), pi.getSlot(), null);
            }
        });

        // availableSlots 은 45 이상, 즉 1페이지 이상일 수 있음.
        int skipPage = availableSlots / 45; // 건너뛸 페이지 수
        int skipSlot = availableSlots % 45; // 건너뛸 슬롯 수
        // 마지막 페이지의 남은 슬롯을 제외한 나머지 슬롯을 막음

        Map<Integer, ItemStack[]> pageItems = inventory.getPageItems();
        for (int page = skipPage; page < pages + 1; page++) {
            int startSlot = (page == skipPage) ? skipSlot : 0;
            for (int slot = startSlot; slot < 45; slot++) {
                if (pageItems.containsKey(page)) {
                    inventory.setPageItem(page, slot, barrier);
                }
            }
        }
        inventory.setPageItems(pageItems);
        inventory.update(); // 인벤토리 업데이트
        inventory.applyChanges(); // 변경 사항 적용
        if (isLookup) {
            inventory.setObj(uuid); // 관리자 조회용으로 유저 UUID 저장
            inventory.setChannel(1);
        } else {
            inventory.setObj(null);
            inventory.setChannel(0);
        }
        inventory.openInventory(p); // 인벤토리 오픈
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("uuid", uuid.toString());
        data.set("maxStorageSlot", maxStorageSlot);
        inventory.serialize(data);
        return data;
    }

    @Override
    public VUser deserialize(YamlConfiguration data) {
        this.uuid = UUID.fromString(data.getString("uuid"));
        this.maxStorageSlot = data.getInt("maxStorageSlot");
        this.inventory = new DInventory("가상 창고", 54, true, true, plugin).deserialize(data);
        return this;
    }

    public boolean addStorageSlots(int slots) {
        if (slots > 0) {
            this.maxStorageSlot += slots;
            return true;
        }
        return false;
    }
}