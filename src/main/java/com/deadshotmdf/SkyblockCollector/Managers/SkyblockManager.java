package com.deadshotmdf.SkyblockCollector.Managers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import org.bukkit.Location;

import java.util.*;

/**
 * Manages skyblock island counts and member functionalities within the game.
 * If the skyblock plugin is enabled
 * Veeeeeeeeeeeeeeery hacky stuff here. Catching throwable means the plugin is not present, but not good practice since, you know, there can be other exceptions.
 * Needs to be changed in the final version
 */
public class SkyblockManager {

    private final HashMap<UUID, Integer> islandCount;

    public SkyblockManager() {
        this.islandCount = new HashMap<>();
    }

    /**
     * Clears all recorded island counts.
     * Used only for configuration reload
     */
    public void reload() {
        this.islandCount.clear();
    }

    /**
     * Retrieves the count for an island at a specified location
     *
     * @param loc The location to check the island count.
     * @return the count of the island or 0 if no island is found or the skyblock plugin is not present.
     */
    public int getIslandCount(Location loc) {
        try {
            Island island = SuperiorSkyblockAPI.getIslandAt(loc);
            return islandCount.computeIfAbsent(island.getUniqueId(), k -> 0);
        } catch (Throwable e) {
            return 0;
        }
    }

    /**
     * Increments the count for an island at a specified location if the skyblock plugin is enabled.
     *
     * @param loc The location of the island to increment the count.
     */
    public void addIslandCount(Location loc) {
        try {
            Island island = SuperiorSkyblockAPI.getIslandAt(loc);
            islandCount.merge(island.getUniqueId(), 1, Integer::sum);
        } catch (Throwable ignored) {}
    }

    /**
     * Decrements the count for an island at a specified location or removes the count if it reaches zero if the skyblock plugin is enabled.
     *
     * @param loc The location of the island to decrement the count.
     */
    public void removeIslandCount(Location loc) {
        try {
            Island island = SuperiorSkyblockAPI.getIslandAt(loc);
            islandCount.computeIfPresent(island.getUniqueId(), (k, v) -> v > 1 ? v - 1 : null);
        } catch (Throwable ignored) {}
    }

    /**
     * Checks if a given UUID is a member of the island at a specified location.
     *
     * @param loc The location of the island.
     * @param uuid The UUID of the player to check.
     * @return true if the player is a member of the island, false otherwise or if an error occurs or the skyblock plugin is not present.
     */
    public boolean isMember(Location loc, UUID uuid) {
        try {
            Island island = SuperiorSkyblockAPI.getIslandAt(loc);
            return island.isMember(SuperiorSkyblockAPI.getPlayer(uuid));
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Removes all collectors owned by a specified UUID and optionally filters by island.
     *
     * @param collectors A map of locations to collectors.
     * @param uuid The UUID of the owner whose collectors are to be removed.
     * @param is The specific island to filter the removal by, or null to remove all owned by the UUID.
     * @return A list of collectors that are eligible for deletion.
     */
    public List<Collector> removeAllCollectors(Map<Location, Collector> collectors, UUID uuid, Object is) {
        List<Collector> list = new LinkedList<>();
        for (Collector collector : collectors.values()) {
            if (!collector.getOwner().equals(uuid))
                continue;

            if (is == null) {
                list.add(collector);
                continue;
            }

            Object compare = getIsland(collector.getLocation());

            if (compare != null && compare.equals(is))
                list.add(collector);
        }

        return list;
    }

    /**
     * Retrieves the island object at a given location.
     *
     * @param loc The location to check.
     * @return The island object if found, null otherwise.
     */
    private Object getIsland(Location loc) {
        try {
            return SuperiorSkyblockAPI.getIslandAt(loc);
        } catch (Throwable e) {
            return null;
        }
    }
}
