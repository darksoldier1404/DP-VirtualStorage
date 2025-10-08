package com.darksoldier1404.dpvs;

import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dpvs.commands.DPVSCommand;
import com.darksoldier1404.dpvs.events.DPVSEvent;
import com.darksoldier1404.dpvs.obj.VUser;

import java.util.UUID;

public class VirtualStorage extends DPlugin {
    public static VirtualStorage plugin;
    public static DataContainer<UUID, VUser> udata;
    public static int defaultStorageSlot;

    public VirtualStorage() {
        super(false);
        plugin = this;
        init();
        udata = loadDataContainer(new DataContainer<>(this, DataType.CUSTOM, "udata"), VUser.class);
        defaultStorageSlot = config.getInt("Settings.defaultStorageSlot", 45);
    }

    public static VirtualStorage getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        PluginUtil.addPlugin(plugin, 27498);
        getCommand("dpvs").setExecutor(new DPVSCommand().getBuilder());
        getServer().getPluginManager().registerEvents(new DPVSEvent(), plugin);
    }

    @Override
    public void onDisable() {
        saveDataContainer();
    }
}
