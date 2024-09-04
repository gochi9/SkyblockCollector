package com.deadshotmdf.SkyblockCollector.Listeners;

import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

//Listener used for the ChunkLoader upgrade. Prevents chunks unloading
public class CancelChunkUnload implements Listener {

    private final CollectorManager collectorManager;

    public CancelChunkUnload(CollectorManager collectorManager) {
        this.collectorManager = collectorManager;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent ev) {
        Location loc = ev.getChunk().getBlock(0,0,0).getLocation();

        Collector collector = collectorManager.getCollectorFromChunk(loc);

        if(collector != null && collector.isCanLoadChunk())
            ev.setCancelled(true);
    }

}
