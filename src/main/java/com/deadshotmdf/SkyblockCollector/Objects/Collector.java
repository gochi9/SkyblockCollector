package com.deadshotmdf.SkyblockCollector.Objects;

import com.deadshotmdf.SkyblockCollector.GUI.GUIModel;
import com.deadshotmdf.SkyblockCollector.SC;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;

import java.util.*;

public class Collector {

    private final UUID owner;
    private final String ownerName;
    private double totalMoneySold;
    private double currentMoney;
    private int currentItemsCount;
    private int totalItemsSold;

    private int radiusLevel;
    private double sellMultiplier;
    private boolean canLoadChunk;

    private Hologram hologram;
    private GUIModel gui;
 //   private long hologramCooldown = 0;

    private final Location location;

    public Collector(UUID owner, Location location) {
        this.owner = owner;
        this.ownerName = owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "None";
        this.totalMoneySold = 0;
        this.totalItemsSold = 0;
        this.radiusLevel = 0;
        this.sellMultiplier = 0;
        this.canLoadChunk = false;
        this.location = location;
    }

    public Collector(UUID owner, double totalMoneySold, int totalItemsSold, int radiusLevel, double sellMultiplier, boolean canLoadChunk, Location location) {
        this.owner = owner;
        this.ownerName = owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "None";
        this.totalMoneySold = totalMoneySold;
        this.totalItemsSold = totalItemsSold;
        this.radiusLevel = radiusLevel;
        this.sellMultiplier = sellMultiplier;
        this.canLoadChunk = canLoadChunk;
        this.location = location;
    }

    public double getCurrentMoney(){
        return currentMoney * sellMultiplier;
    }

    public void addCurrentMoney(double money){
        currentMoney += money;
    }

    public int getCurrentItemsSize(){
        return currentItemsCount;
    }

    public void addItems(int amount){
        currentItemsCount += amount;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName(){
        return ownerName;
    }

    public double getTotalMoneySold() {
        return totalMoneySold;
    }

    public void addTotalMoneySold(double totalMoneySold) {
        this.totalMoneySold += totalMoneySold;
    }

    public int getTotalItemsSold() {
        return totalItemsSold;
    }

    public void addTotalItemsSold(int totalItemsSold) {
        this.totalItemsSold += totalItemsSold;
    }

    public int getRadiusLevel() {
        return radiusLevel;
    }

    public void addRadiusLevel(int radiusLevel) {
        this.radiusLevel += radiusLevel;
    }

    public boolean isCanLoadChunk() {
        return canLoadChunk;
    }

    public void setCanLoadChunk(boolean canLoadChunk) {
        this.canLoadChunk = canLoadChunk;
    }

    public double getSellMultiplier() {
        return sellMultiplier;
    }

    public void addSellMultiplier(double sellMultiplier) {
        this.sellMultiplier += sellMultiplier;
    }

    public void createHologram(Hologram hologram){
        this.hologram = hologram;
    }

    public void removeHologram(){
        if(this.hologram != null)
            this.hologram.delete();
    }

    public Hologram getHologram(){
        return hologram;
    }

    public Location getLocation(){
        return location;
    }

    public void setGui(GUIModel gui){
        this.gui = gui;
    }

    public GUIModel getGui(){
        return gui;
    }

    public void removeGUI(){
        if(gui == null)
            return;

        new HashSet<>(gui.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
        this.setGui(null);
    }

    public void sellAll(){
        addTotalMoneySold(getCurrentMoney());
        addTotalItemsSold(currentItemsCount);
        SC.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(owner), getCurrentMoney());
        this.currentMoney = 0;
        this.currentItemsCount = 0;
    }

//    public boolean canUpdateHologram(){
//        if(hologramCooldown >= System.currentTimeMillis())
//            return false;
//
//        this.hologramCooldown = System.currentTimeMillis() + 2500;
//        return true;
//    }
}
