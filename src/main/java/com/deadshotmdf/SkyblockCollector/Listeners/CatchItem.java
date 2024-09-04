package com.deadshotmdf.SkyblockCollector.Listeners;

import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Utils.ItemUtils;
import gcspawners.NBTItem;
import org.bukkit.Location;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class CatchItem implements Listener {

    private final CollectorManager collectorManager;
    //List used to prevent player items from being collected accidentally
    //Not the best way possible. Would be better to mark the items on death with an NBT tag, and then remove that tag on ItemSpawnEvent.
    private final List<ItemStack> droppedByPlayer;

    public CatchItem(CollectorManager collectorManager) {
        this.collectorManager = collectorManager;
        //ArrayList is the worst thing you could use here. Keep it a LinkedList<>()
        this.droppedByPlayer = new LinkedList<>();
    }

    //If this is causing lag because of the ShopGUI plugin, suggest to create your own pricing system, use EnumMap to store the Material as the key and the price as the value. That should improve the lag problem
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent ev) {
        Item ent = ev.getEntity();
        ItemStack item = ent.getItemStack();

        if(item == null)
            return;

        //List#remove() returns true if the element existed in the list
        //Meaning we can both check if the item is present in the list and remove it at the same time
        //If the item exists in this list, it means that it's a player item, so we need to return the event here to prevent important items from being collected
        if(droppedByPlayer.remove(item) || new NBTItem(item).hasKey("SkyblockCollectorIdent"))
            return;

        double value = ItemUtils.getItemValue(item);

        //There's no reason to collect items with no value
        if(value <= 0.0D)
            return;

        Location loc = ent.getLocation();
        Collector collector = collectorManager.getCollectorFromChunk(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4);

        if(collector == null)
            return;

        collector.addCurrentMoney(value);
        collector.addItems(item.getAmount());
        collectorManager.refreshHologram(collector);
        ent.remove();
        ev.setCancelled(true);
    }

    //Prevents player items from being collected
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev){
        droppedByPlayer.addAll(ev.getDrops());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent ev){
        ItemStack item = ev.getItemDrop().getItemStack();

        if(item != null)
            droppedByPlayer.add(ev.getItemDrop().getItemStack());
    }

}
