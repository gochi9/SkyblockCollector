package com.deadshotmdf.SkyblockCollector.Storage.db;

import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import org.bukkit.Location;

import java.util.*;

public abstract class CollectorDao {

    public abstract void saveAllCollectors(Map<Location, Collector> collectors);
    public abstract Map<Location, Collector> loadAllCollectors();

}
