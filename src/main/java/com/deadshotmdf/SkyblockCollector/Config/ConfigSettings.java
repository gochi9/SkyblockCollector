package com.deadshotmdf.SkyblockCollector.Config;

import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Objects.LimitType;
import com.deadshotmdf.SkyblockCollector.SC;
import com.deadshotmdf.SkyblockCollector.Utils.ItemUtils;
import com.deadshotmdf.SkyblockCollector.Utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigSettings {

    //Task
    private static int autoSellTaskDelay;

    //Message
    private static String reloadConfig;
    private static String noPermission;
    private static String giveItemSelf;
    private static String giveItem;
    private static String giveItemAll;
    private static String receiveItem;
    private static String playerOffline;
    private static String tooPoor;
    private static String upgradePurchased;
    private static String cannotPlaceCollector;
    private static String playerLimitReached;
    private static String islandLimitReached;
    private static String bothLimitReached;

    //Collector
    private static Material collectorMaterial;
    private static int collectorMaterialData;
    private static String collectorItemName;
    private static List<String> collectorItemLore;

    //Hologram
    private static List<String> hologramLines;
    private static double hologramYOffSet;

    //Upgrades
    private static String maxText;

    private static int maxRadius;
    private static double radiusInitialCost;
    private static double radiusCostGrowth;

    private static double maxSellMultiplier;
    private static double sellMultiplierInitialCost;
    private static double sellMultiplierCostGrowth;
    private static double sellMultiplierAdd;

    private static double chunkLoaderPrice;

    //Place limit
    private static int playerLimit;
    private static int islandLimit;
    private static LimitType limitType;

    //GUI
    private static String GUIName;
    private static int GUISize;
    private static HashMap<Integer, ItemStack> items;

    //Placeholders
    private final static String[] searchList = {"{player}", "{money}", "{items}", "{total_money_sold}", "{total_items_sold}", "{currentRadius}", "{maxRadius}", "{sellMultiplier}", "{maxSellMultiplier}", "{canLoadChunks}", "{radiusNextUpgradeCost}", "{sellMultiplierNextUpgradeCost}", "{currentSellMultiplierLevel}", "{maxSellMultiplierLevel}", "{chunkLoaderPrice}"};

    //
    //Getters
    //

    //Task
    public static int getAutoSellTaskDelay(){
        return autoSellTaskDelay;
    }

    //
    //Message
    public static String getReloadConfig(){
        return reloadConfig;
    }

    public static String getNoPermission() {
        return noPermission;
    }

    public static String getGiveItemSelf() {
        return giveItemSelf;
    }

    public static String getGiveItem(String player) {
        return giveItem.replace("{player}", player);
    }

    public static String getGiveItemAll() {
        return giveItemAll;
    }

    public static String getReceiveItem(String player) {
        return receiveItem.replace("{player}", player);
    }

    public static String getPlayerOffline(String player){
        return playerOffline.replace("{player}", player);
    }

    public static String getTooPoor(double cost, double balance){
        return tooPoor.replace("{cost}", s(cost)).replace("{balance}", s(balance));
    }

    public static String getUpgradePurchased(double cost, double balance){
        return upgradePurchased.replace("{cost}", s(cost)).replace("{balance}", s(balance));
    }

    public static String getPlayerLimitReached(){
        return playerLimitReached.replace("{playerLimit}", s(playerLimit)).replace("{islandLimit}", s(islandLimit));
    }

    public static String getIslandLimitReached(){
        return islandLimitReached.replace("{playerLimit}", s(playerLimit)).replace("{islandLimit}", s(islandLimit));
    }

    public static String getBothLimitReached(){
        return bothLimitReached.replace("{playerLimit}", s(playerLimit)).replace("{islandLimit}", s(islandLimit));
    }

    //
    //Collector
    public static String getCannotPlaceCollector(){
        return cannotPlaceCollector;
    }

    public static Material getCollectorMaterial() {
        return collectorMaterial;
    }

    public static int getCollectorMaterialData() {
        return collectorMaterialData;
    }

    public static String getCollectorItemName(Collector collector) {
        return collectorReplace(collectorItemName, collector);
    }

    public static List<String> getCollectorItemLore(Collector collector) {
        List<String> itemLore = new LinkedList<>();
        for (String line : collectorItemLore)
            itemLore.add(collectorReplace(line, collector));

        return itemLore;
    }

    //
    //Hologram
    public static List<String> getHologramLines(Collector collector) {
        List<String> updatedHologramLines = new LinkedList<>();
        for (String line : hologramLines)
            updatedHologramLines.add(collectorReplace(line, collector));

        return updatedHologramLines;
    }

    public static double getHologramYOffSet() {
        return hologramYOffSet;
    }

    //
    //Upgrade
    public static int getMaxRadius() {
        return maxRadius;
    }

    //###Next level price
    public static double calculateUpgradeCost(int currentLevel) {
        if (currentLevel >= maxRadius)
            return -1;

        return (radiusInitialCost * Math.pow(radiusCostGrowth, currentLevel - 1));
    }


    public static double getSellMultiplierAdd() {
        return sellMultiplierAdd;
    }

    public static double getMaxSellMultiplier() {
        return maxSellMultiplier;
    }
    
    //###Next level price
    public static double calculateSellMultiplierCost(double currentMultiplier) {
        if (currentMultiplier >= maxSellMultiplier)
            return -1;

        double level = getSellMultiplierLevel(currentMultiplier);
        if (level == 0)
            return sellMultiplierInitialCost;
        else
            return (sellMultiplierInitialCost * Math.pow(sellMultiplierCostGrowth, level - 1));
    }

    public static int getMaxSellMultiplierLevel() {
        return (int) ((maxSellMultiplier - 1) / sellMultiplierAdd);
    }

    public static int getSellMultiplierLevel(double currentMultiplier){
        return (int) ((currentMultiplier - 1) / sellMultiplierAdd);
    }



    public static double getChunkLoaderPrice(){
        return chunkLoaderPrice;
    }

    //
    //GUI
    public static String getGUIName(Collector collector){
        return collectorReplace(GUIName, collector);
    }

    public static int getGUISize(){
        return GUISize;
    }

    public static HashMap<Integer, ItemStack> getItems(){
        return items;
    }

    //
    //Place limit
    public static int getPlayerLimit(){
        return playerLimit;
    }

    public static int getIslandLimit(){
        return islandLimit;
    }

    public static LimitType getLimitType(){
        return limitType;
    }

    //
    //Reload config
    public static void reloadConfig(SC main){
        main.reloadConfig();
        main.saveDefaultConfig();

        FileConfiguration config = main.getConfig();
        //Task
        autoSellTaskDelay = config.getInt("autoSellTaskDelay") * 20;

        //Message
        reloadConfig = color(config.getString("reloadConfig"));
        noPermission = color(config.getString("noPermission"));
        giveItemSelf = color(config.getString("giveItemSelf"));
        giveItem = color(config.getString("giveItem"));
        giveItemAll = color(config.getString("giveItemAll"));
        receiveItem = color(config.getString("receiveItem"));
        playerOffline = color(config.getString("playerOffline"));
        tooPoor = color(config.getString("tooPoor"));
        upgradePurchased = color(config.getString("upgradePurchased"));
        cannotPlaceCollector = color(config.getString("cannotPlaceCollector"));
        playerLimitReached = color(config.getString("playerLimitReached"));
        islandLimitReached = color(config.getString("islandLimitReached"));
        bothLimitReached = color(config.getString("bothLimitReached"));
        
        //Collector
        collectorMaterial = Utils.getMaterial(config.getString("collectorMaterial"), Material.BEACON);
        collectorMaterialData = config.getInt("collectorMaterialData");
        collectorItemName = color(config.getString("collectorItemName"));
        collectorItemLore = color(config.getStringList("collectorItemLore"));

        //Hologram
        hologramLines = color(config.getStringList("hologramLines"));
        hologramYOffSet = config.getDouble("hologramYOffSet");

        //Upgrade
        maxText = color(config.getString("maxText"));

        maxRadius = config.getInt("maxRadius");
        radiusInitialCost = config.getInt("radiusInitialCost");
        radiusCostGrowth = config.getInt("radiusCostGrowth");

        maxSellMultiplier = config.getDouble("maxSellMultiplier");
        sellMultiplierAdd = config.getDouble("sellMultiplierAdd");
        sellMultiplierInitialCost = config.getDouble("sellMultiplierInitialCost");
        sellMultiplierCostGrowth = config.getDouble("sellMultiplierCostGrowth");

        chunkLoaderPrice = config.getDouble("chunkLoaderPrice");

        //Place limit
        playerLimit = config.getInt("playerLimit");
        islandLimit = config.getInt("islandLimit");
        limitType = LimitType.fromValue(config.getInt("limitMode"));

        //GUI
        GUIName = color(config.getString("GUIName"));
        GUISize = config.getInt("GUISize");
        items = new HashMap<>();

        //Filling the GUI preset items
        ConfigurationSection sec = config.getConfigurationSection("items");

        if(sec == null)
            return;

        Set<String> keys = sec.getKeys(false);

        if(keys == null || keys.isEmpty())
            return;

        for(String key : keys)
            ItemUtils.createItemStack(config, key, items);
    }

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s != null ? s : "invalid_string");
    }

    public static List<String> color(List<String> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();

        return list.stream().map(ConfigSettings::color).collect(Collectors.toList());
    }

    private static String s(Object i){
        if(i instanceof Double)
            return Utils.formatDouble((Double)i);

        return String.valueOf(i);
    }

    //Using Apache3's replace string for this since java's replace method is ass and this text is refreshed often
    public static String collectorReplace(String line, Collector collector){
        int radius = collector.getRadiusLevel();
        double radiusCost = calculateUpgradeCost(radius);

        double sellMultiplier = collector.getSellMultiplier();
        double sellMultiplierCost = calculateSellMultiplierCost(sellMultiplier);

        boolean canChunk = collector.isCanLoadChunk();

        return StringUtils.replaceEach(line, searchList,
                new String[]{collector.getOwnerName(),
                        s(collector.getCurrentMoney()),
                        s(collector.getCurrentItemsSize()),
                        s(collector.getTotalMoneySold()),
                        s(collector.getTotalItemsSold()),
                        s(radius),
                        s(maxRadius),
                        s(sellMultiplier),
                        s(maxSellMultiplier),
                        s(canChunk),
                        s(radiusCost != -1 ? radiusCost : maxText),
                        s(sellMultiplierCost != -1.0 ? sellMultiplierCost : maxText),
                        s(getSellMultiplierLevel(sellMultiplier)),
                        s(getMaxSellMultiplierLevel()),
                        s(!canChunk ? chunkLoaderPrice : maxText)});

    }

}
