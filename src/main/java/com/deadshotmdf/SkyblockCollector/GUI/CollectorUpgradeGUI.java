package com.deadshotmdf.SkyblockCollector.GUI;

import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Objects.UpgradeType;
import com.deadshotmdf.SkyblockCollector.Utils.ItemUtils;
import com.deadshotmdf.SkyblockCollector.Utils.Utils;
import de.tr7zw.nbtapi.NBTItem;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectorUpgradeGUI implements GUIModel{

    private final Inventory inv;
    private final Collector collector;
    private final CollectorManager manager;

    public CollectorUpgradeGUI(Inventory inv, Collector collector, CollectorManager manager){
        this.inv = inv;
        this.collector = collector;
        this.manager = manager;
        refreshInventory();
    }

    public Inventory getInventory(){
        return inv;
    }

    public void onOpen(InventoryOpenEvent ev){
        return;
    }

    public void onClick(InventoryClickEvent ev){
        ev.setCancelled(true);

        ItemStack i = ev.getCurrentItem();

        if(i == null)
            return;

        UpgradeType type = UpgradeType.getUpgradeType(new NBTItem(i).getString("SkyblockCollectorSFSDF"));

        if(type == null)
            return;

        int currentLevel, maxLevel;
        double cost;
        switch (type){
            case RADIUS:
                currentLevel = collector.getRadiusLevel();
                maxLevel = ConfigSettings.getMaxRadius();
                cost = ConfigSettings.calculateUpgradeCost(currentLevel);
                break;
            case SELL_MULTIPLIER:
                currentLevel = ConfigSettings.getSellMultiplierLevel(collector.getSellMultiplier());
                maxLevel = ConfigSettings.getMaxSellMultiplierLevel();
                cost = ConfigSettings.calculateSellMultiplierCost(collector.getSellMultiplier());
                break;
            case CHUNK_LOADER:
                currentLevel = collector.isCanLoadChunk() ? 1 : 0;
                maxLevel = 1;
                cost = ConfigSettings.getChunkLoaderPrice();
                break;
            default:
                currentLevel = 0;
                maxLevel = 0;
                cost = Integer.MAX_VALUE;
        }

        if(currentLevel >= maxLevel || !manager.canUpgradeRadius(ev.getWhoClicked(), type, collector) || !Utils.deductPayment((Player) ev.getWhoClicked(), cost))
            return;

        if(manager.upgradeCollector(collector, type))
            refreshInventory();
    }

    public void onClose(InventoryCloseEvent ev){
        return;
    }

    public void refreshInventory(){
        inv.clear();

        for(Map.Entry<Integer, ItemStack> entry : ConfigSettings.getItems().entrySet()){
            Integer slot = entry.getKey();
            ItemStack item = entry.getValue();

            if(slot == null || item == null)
                continue;

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ConfigSettings.collectorReplace(meta.getDisplayName(), collector));

            List<String> lore = new LinkedList<>(), currentLore = meta.hasLore() ? meta.getLore() : lore;

            for(String line : currentLore)
                lore.add(ConfigSettings.collectorReplace(line, collector));

            meta.setLore(lore);
            inv.setItem(slot, ItemUtils.liteClone(item, meta));
        }
    }

}
