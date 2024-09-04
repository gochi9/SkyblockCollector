package com.deadshotmdf.SkyblockCollector.Listeners;

import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Utils.ItemUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class BlockBreak implements Listener {

    private final CollectorManager collectorManager;

    public BlockBreak(CollectorManager collectorManager) {
        this.collectorManager = collectorManager;
    }

    //Breaking the collector
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent ev) {
        if(ev.isCancelled())
            return;

        Location loc = ev.getBlock().getLocation();
        Collector collector = collectorManager.getCollector(loc);

        if(collector == null)
            return;

        ItemStack item = ItemUtils.createCollectorItem(collector);
        collectorManager.removeCollector(collector);
        loc.getWorld().dropItemNaturally(loc, item);
        ev.setCancelled(true);
        ev.getBlock().setType(Material.AIR);
    }

    //Events below are used to stop the collector from exploding or being moved by other means
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent ev){
        doNotRemove(ev.blockList());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent ev){
        doNotRemove(ev.blockList());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonMove(BlockPistonExtendEvent ev){
        ev.setCancelled(cancel(ev.getBlocks()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent ev){
        ev.setCancelled(cancel(ev.getBlocks()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonRetract(EntityChangeBlockEvent ev){
        Block block = ev.getBlock();

        if(block.getType() != ConfigSettings.getCollectorMaterial())
            return;

        ev.setCancelled(cancel(Collections.singletonList(block)));
    }

    //Helper methods
    private void doNotRemove(List<Block> blockList){
        blockList.removeIf(block -> collectorManager.getCollector(block.getLocation()) != null);
    }

    private boolean cancel(List<Block> blockList){
        for(Block block : blockList){
            if(collectorManager.getCollector(block.getLocation()) != null)
                return true;
        }

        return false;
    }
}
