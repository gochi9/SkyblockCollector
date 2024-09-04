package com.deadshotmdf.SkyblockCollector.Tasks;

import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.SC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;

public class AutoSellTask implements Runnable{

    private final SC main;
    private final CollectorManager collectorManager;
    private final BukkitScheduler scheduler;

    public AutoSellTask(SC main, CollectorManager collectorManager) {
        this.main = main;
        this.collectorManager = collectorManager;
        this.scheduler = Bukkit.getScheduler();
    }

    public void run(){
        Map<Location, Collector> toSell = new HashMap<>();

        for(Map.Entry<Location, Collector> entry : collectorManager.getCollectors().entrySet()) {
            Collector collector = entry.getValue();
            if (collector.getCurrentItemsSize() > 0 && collector.getCurrentMoney() > 0.0d)
                toSell.put(entry.getKey(), collector);
        }
        scheduler.runTask(main, () -> continueSync(toSell));
    }

    private void continueSync(Map<Location, Collector> collectors){
        collectors.forEach((location, collector) -> {
            if(collector != null && collectorManager.getCollectors().containsValue(collector)){
                collector.sellAll();
                collectorManager.refreshHologram(collector);
            }
        });
    }

}
