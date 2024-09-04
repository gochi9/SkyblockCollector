package com.deadshotmdf.SkyblockCollector.Listeners;

import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Objects.LimitType;
import com.deadshotmdf.SkyblockCollector.Utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class CollectorPlace implements Listener {

    private final CollectorManager collectorManager;

    public CollectorPlace(CollectorManager collectorManager) {
        this.collectorManager = collectorManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent ev) {
        if(ev.isCancelled())
            return;

        Player player = ev.getPlayer();
        Collector collector = ItemUtils.getCollectorFromItem(player.getUniqueId(), ev.getItemInHand(), ev.getBlockPlaced().getLocation());
        LimitType type;

        //Hacky stuff, needs replaced
        //LimitType == DISABLED means the collector was placed without issue
        if(collector == null || (type = collectorManager.placeCollector(collector)) == LimitType.DISABLED)
            return;

        ev.setCancelled(true);

        switch (type){
            case CLASHING:
                player.sendMessage(ConfigSettings.getCannotPlaceCollector());
                break;
            case PLAYER_LIMIT:
                player.sendMessage(ConfigSettings.getPlayerLimitReached());
                break;
            case ISLAND_LIMIT:
                player.sendMessage(ConfigSettings.getIslandLimitReached());
                break;
            case BOTH:
                player.sendMessage(ConfigSettings.getBothLimitReached());
                break;
        }
    }
}
