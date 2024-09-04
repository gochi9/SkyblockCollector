package com.deadshotmdf.SkyblockCollector.Managers;

import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.Objects.LimitType;
import com.deadshotmdf.SkyblockCollector.Objects.UpgradeType;
import com.deadshotmdf.SkyblockCollector.Storage.db.CollectorDao;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all collector-related operations such as placing, removing, upgrading collectors,
 * and managing their configurations and storage.
 */
public class CollectorManager {

    private final CollectorDao database;
    private final HolographicDisplaysAPI hologramAPI;
    private final GUIManager guiManager;
    private final ConcurrentHashMap<Location, Collector> collectors;
    private final HashMap<Location, Location> chunksAffected;
    private final HashMap<UUID, Integer> collectorsPlaced;
    private final boolean isSkyblockEnabled;
    private final SkyblockManager skyblockManager;

    public CollectorManager(CollectorDao database, HolographicDisplaysAPI hologramAPI, GUIManager guiManager, SkyblockManager skyblockManager) {
        this.database = database;
        this.hologramAPI = hologramAPI;
        this.guiManager = guiManager;
        this.collectors = new ConcurrentHashMap<>(database.loadAllCollectors());
        this.chunksAffected = new HashMap<>();
        this.collectorsPlaced = new HashMap<>();
        this.isSkyblockEnabled = Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2");
        this.skyblockManager = skyblockManager;
        reloadConfig();
    }

    /**
     * Retrieves the collector located at a specific chunk.
     */
    public Collector getCollectorFromChunk(World w, int chunkX, int chunkZ) {
        return getCollectorFromChunk(new Location(w, chunkX << 4, 0, chunkZ << 4));
    }

    /**
     * Retrieves the collector located at a specific chunk.
     */
    public Collector getCollectorFromLocationChunk(Location loc) {
        return getCollectorFromChunk(new Location(loc.getWorld(), loc.getBlockX() >> 4 << 4, 0, loc.getBlockZ() >> 4 << 4));
    }

    /**
     * Retrieves the collector located at a specific chunk location (Chunk#getBlock(0,0,0))
     */
    public Collector getCollectorFromChunk(Location loc) {
        Location possibleCollectorLocation = chunksAffected.get(loc);
        return possibleCollectorLocation == null ? null : collectors.get(possibleCollectorLocation);
    }

    /**
     * Retrieves a collector directly from a specified location.
     */
    public Collector getCollector(Location loc) {
        return collectors.get(loc);
    }

    /**
     * Attempts to place a new collector in the world at the specified location.
     */
    public LimitType placeCollector(Location loc, UUID owner, int totalMoneySold, int totalItemsSold, int radius, int sellMultiplier, boolean canLoadChunks) {
        Collector collector = new Collector(owner, totalMoneySold, totalItemsSold, radius, sellMultiplier, canLoadChunks, loc);
        return placeCollector(collector);
    }

    /**
     * Attempts to place a collector in the world, affecting game chunks and updating internal states.
     * LimitType is used to both determine the limitation and to send the message for the limitation
     * DISABLED here means it can go ahead and place the collector
     */
    public LimitType placeCollector(Collector collector) {
        Location loc = collector.getLocation();
        LimitType type = canPlaceLimit(collector.getOwner(), loc);
        if (type != LimitType.DISABLED)
            return type;

        World w = loc.getWorld();
        int chunkX = loc.getBlockX() >> 4;
        int chunkZ = loc.getBlockZ() >> 4;
        Set<Location> affected = getAffectedChunks(loc, w, chunkX, chunkZ, collector.getRadiusLevel(), collector.isCanLoadChunk(), true);
        if (affected == null)
            return LimitType.CLASHING;

        affected.forEach(location -> chunksAffected.put(location, loc));
        collectors.put(loc, collector);
        guiManager.registerCollectorInventory(collector, this);
        collectorsPlaced.merge(collector.getOwner(), 1, Integer::sum);
        skyblockManager.addIslandCount(loc);
        refreshCollector(collector);
        return type;
    }

    /**
     * Removes a collector from the world and cleans up related resources and states.
     */
    public void removeCollector(Collector collector) {
        if (collector == null)
            return;

        Location loc = collector.getLocation();
        collector.sellAll();
        collector.removeHologram();
        collectors.remove(loc);

        World w = loc.getWorld();
        int chunkX = loc.getBlockX() >> 4;
        int chunkZ = loc.getBlockZ() >> 4;
        //This cannot be null if lookOut is false
        (getAffectedChunks(loc, w, chunkX, chunkZ, collector.getRadiusLevel(), false, false))
                .forEach(chunksAffected::remove);

        collectorsPlaced.computeIfPresent(collector.getOwner(), (k, v) -> v > 1 ? v - 1 : null);
        skyblockManager.removeIslandCount(loc);
        loc.getBlock().setType(Material.AIR);
    }

    /**
     * Creates the collector's display
     */
    public void refreshCollector(Collector collector) {
        Location loc = collector.getLocation();
        loc.getBlock().setType(ConfigSettings.getCollectorMaterial());
        collector.removeHologram();
        Hologram hologram = hologramAPI.createHologram(loc.clone().add(0.5, ConfigSettings.getHologramYOffSet(), 0.5));
        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
        updateHologramLines(collector, hologram);
        collector.createHologram(hologram);
    }

    /**
     * Refreshes the hologram for a collector, updating lines as necessary.
     */
    public void refreshHologram(Collector collector) {
        if (collector == null)
            return;

        Location loc = collector.getLocation();

        if(loc == null)
            return;

        if (!collectors.containsKey(loc))
            return;

        Hologram hologram = collector.getHologram();
        if (hologram == null) {
            refreshCollector(collector);
            return;
        }

        hologram.getLines().clear();
        updateHologramLines(collector, hologram);
    }

    /**
     * Determines if a collector's radius can be upgraded, ensuring no overlap with existing collectors.
     */
    public boolean canUpgradeRadius(HumanEntity player, UpgradeType type, Collector collector) {
        if (type != UpgradeType.RADIUS)
            return true;

        int radius = collector.getRadiusLevel() + 1;
        Location loc = collector.getLocation();
        boolean value = getAffectedChunks(loc, loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4, radius, false, true) != null;

        if (!value)
            player.sendMessage(ConfigSettings.getCannotPlaceCollector());
        return value;
    }

    /**
     * Upgrades a collector based on the specified upgrade type.
     * Returns true if the upgrade GUI should refresh
     * We don't need to refresh the GUI when upgrading radius since the collector is essentially remade, including the GUI. It's a waste
     *
     * "Why not re-use the GUI already present?"
     * Has a small problem with the way I did it cause I'm a fucking fool. But the performance benefit is not noticeable at all to be worth the work
     */
    public boolean upgradeCollector(Collector collector, UpgradeType type) {
        boolean refresh = true;
        switch (type) {
            case RADIUS:
                removeCollector(collector);
                collector.addRadiusLevel(1);
                placeCollector(collector);
                refresh = false;
                break;
            case SELL_MULTIPLIER:
                collector.addSellMultiplier(ConfigSettings.getSellMultiplierAdd());
                break;
            case CHUNK_LOADER:
                collector.setCanLoadChunk(true);
                break;
        }

        refreshHologram(collector);
        return refresh;
    }

    /**
     * Updates hologram lines to reflect current state or changes to the collector.
     */
    private void updateHologramLines(Collector collector, Hologram hologram) {
        HologramLines lines = hologram.getLines();
        ConfigSettings.getHologramLines(collector).forEach(lines::appendText);
    }

    /**
     * Determines the set of chunks affected by the placement or radius upgrade of a collector.
     */
    private Set<Location> getAffectedChunks(Location original, World w, int chunkX, int chunkZ, int radius, boolean loadChunk, boolean lookOut) {
        //Using set to prevent multiple instance of the same Location
        Set<Location> set = new HashSet<>();
        for (int x = chunkX - radius; x <= chunkX + radius; x++)
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                Location loc = new Location(w, x << 4, 0, z << 4);

                if (lookOut) {
                    Location exists = chunksAffected.get(loc);
                    if (exists != null && !exists.equals(original))
                        return null;
                }

                if (loadChunk) {
                    Chunk c = loc.getChunk();
                    if (!c.isLoaded())
                        c.load();
                }

                set.add(loc);
            }
        return set;
    }

    /**
     * Determines the placement limit based on player and island limits set in configurations.
     */
    private LimitType canPlaceLimit(UUID owner, Location island) {
        LimitType limit = ConfigSettings.getLimitType();
        if (limit == LimitType.DISABLED || (limit == LimitType.ISLAND_LIMIT && !isSkyblockEnabled))
            return LimitType.DISABLED;

        int blocksPlacedPlayer = collectorsPlaced.computeIfAbsent(owner, k -> 0);
        int blocksPlacedIsland = skyblockManager.getIslandCount(island);

        switch (limit) {
            case PLAYER_LIMIT:
                if (blocksPlacedPlayer >= ConfigSettings.getPlayerLimit())
                    return limit;
            case ISLAND_LIMIT:
                if (blocksPlacedIsland >= ConfigSettings.getIslandLimit())
                    return limit;
        }

        return blocksPlacedPlayer >= ConfigSettings.getPlayerLimit() || blocksPlacedIsland >= ConfigSettings.getIslandLimit() ? limit : LimitType.DISABLED;
    }

    /**
     * Reloads the collector configurations, clears the internal state, and reinitializes everything.
     */
    public void reloadConfig() {
        collectorsPlaced.clear();
        chunksAffected.clear();
        skyblockManager.reload();
        guiManager.reload();
        Set<Collector> clone = new HashSet<>(collectors.values());
        collectors.clear();
        clone.forEach(this::placeCollector);
    }

    /**
     * Saves all collector data to storage.
     */
    public void saveAll() {
        database.saveAllCollectors(collectors);
    }

    /**
     * Returns the current map of all collectors.
     */
    public ConcurrentHashMap<Location, Collector> getCollectors() {
        return collectors;
    }

}