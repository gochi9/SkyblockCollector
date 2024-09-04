package com.deadshotmdf.SkyblockCollector.Listeners;

import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Managers.SkyblockManager;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

//Listener used to cancel interaction with the base block of the collector, and to open up the GUI if player is the owner, or a member of the same island
public class CancelInteract implements Listener {

    private final CollectorManager manager;
    private final SkyblockManager skyblockManager;

    public CancelInteract(CollectorManager manager, SkyblockManager skyblockManager) {
        this.manager = manager;
        this.skyblockManager = skyblockManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent ev) {
        if(ev.isCancelled() || ev.useInteractedBlock() != Event.Result.ALLOW || ev.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = ev.getClickedBlock();

        if(block == null)
            return;

        Location loc = block.getLocation();
        Collector collector = manager.getCollector(loc);

        if(collector == null)
            return;

        ev.setCancelled(true);
        ev.setUseInteractedBlock(Event.Result.DENY);
        Player player = ev.getPlayer();
        UUID uuid = player.getUniqueId();

        if(!collector.getOwner().equals(uuid) && !skyblockManager.isMember(loc, uuid))
            return;

        player.openInventory(collector.getGui().getInventory());
    }

}
