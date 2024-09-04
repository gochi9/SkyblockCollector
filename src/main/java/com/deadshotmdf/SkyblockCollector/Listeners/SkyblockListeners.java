package com.deadshotmdf.SkyblockCollector.Listeners;

import com.bgsoftware.superiorskyblock.api.events.*;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Managers.SkyblockManager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

//Extra checks if the island plugin is present
public class SkyblockListeners implements Listener {

    private final SkyblockManager skyblockManager;
    private final CollectorManager collectorManager;

    public SkyblockListeners(SkyblockManager skyblockManager, CollectorManager collectorManager) {
        this.skyblockManager = skyblockManager;
        this.collectorManager = collectorManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIslandLeave(IslandDisbandEvent ev){
        if(ev.isCancelled())
            return;

        for(Chunk c : ev.getIsland().getAllChunks())
            collectorManager.removeCollector(collectorManager.getCollectorFromChunk(c.getBlock(0,0,0).getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIslandKick(IslandKickEvent ev){
        if(ev.isCancelled())
            return;

        removePlayer(ev.getTarget().getUniqueId(), ev.getIsland());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIslandBan(IslandBanEvent ev){
        if(ev.isCancelled())
            return;

        removePlayer(ev.getTarget().getUniqueId(), ev.getIsland());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIslandUncoop(IslandUncoopPlayerEvent ev){
        if(ev.isCancelled())
            return;

        removePlayer(ev.getTarget().getUniqueId(), ev.getIsland());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIslandQuit(IslandQuitEvent ev){
        if(ev.isCancelled())
            return;

        removePlayer(ev.getPlayer().getUniqueId(), ev.getIsland());
    }

    private void removePlayer(UUID uuid, Island island){
        skyblockManager.removeAllCollectors(collectorManager.getCollectors(), uuid, island).forEach(collectorManager::removeCollector);
    }

}
