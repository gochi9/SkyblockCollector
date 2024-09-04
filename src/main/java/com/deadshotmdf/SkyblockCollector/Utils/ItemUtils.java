package com.deadshotmdf.SkyblockCollector.Utils;

import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Objects.UpgradeType;
import de.tr7zw.nbtapi.NBTItem;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ItemUtils {

    public static List<Integer> parseRanges(String input) {
        return Arrays.stream(input.split(","))
                .flatMap(part -> {
                    String[] range = part.trim().split("-");
                    if (range.length == 2){
                        int min = Utils.getInteger(range[0].trim(), 0), max = Utils.getInteger(range[1].trim(), 1);

                        return IntStream.rangeClosed(Math.min(min, max), Math.max(min, max)).boxed();
                    }
                    else
                        return Stream.of(Utils.getInteger(part.trim(), 0));
                })
                .collect(Collectors.toList());
    }

    public static void createItemStack(FileConfiguration config, String key, HashMap<Integer, ItemStack> items){
        String name = ConfigSettings.color(config.getString("items." + key + ".name"));
        List<String> lore = ConfigSettings.color(config.getStringList("items." + key + ".lore"));
        Material material = Utils.getMaterial(config.getString("items." + key + ".material"), Material.PAPER);
        short data = (short) config.getInt("items." + key + ".materialData");
        List<Integer> slots = parseRanges(config.getString("items." + key + ".slot"));

        ItemStack item = new ItemStack(material, 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);

        UpgradeType type = UpgradeType.getUpgradeType(config.getString("items." + key + ".upgrade"));

        NBTItem nbt = new NBTItem(item);

        if(type != null)
            nbt.setString("SkyblockCollectorSFSDF", type.toString());

        slots.forEach(slot ->  items.put(slot, nbt.getItem()));
    }

    public static Collector getCollectorFromItem(UUID uuid, ItemStack item, Location location) {
        if(uuid == null || item == null || location == null)
            return null;

        NBTItem nbt = new NBTItem(item);

        if(!nbt.hasKey("SkyblockCollectorIdent"))
            return null;

        return new Collector(uuid, nbt.getDouble("totalMoneySold"), nbt.getInteger("totalItemsSold"), nbt.getInteger("radiusLevel"), nbt.getInteger("sellMultiplier"), nbt.getBoolean("canLoadChunk"), location);
    }

    public static ItemStack createCollectorItem(Collector collector){
        ItemStack item = new ItemStack(ConfigSettings.getCollectorMaterial(), 1, (short) ConfigSettings.getCollectorMaterialData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ConfigSettings.getCollectorItemName(collector));
        meta.setLore(ConfigSettings.getCollectorItemLore(collector));
        item.setItemMeta(meta);

        NBTItem nbt = new NBTItem(item);
        nbt.setBoolean("SkyblockCollectorIdent", true);
        nbt.setDouble("totalMoneySold", collector.getTotalMoneySold());
        nbt.setInteger("totalItemsSold", collector.getTotalItemsSold());
        nbt.setInteger("radiusLevel", collector.getRadiusLevel());
        nbt.setDouble("sellMultiplier", collector.getSellMultiplier());
        nbt.setBoolean("canLoadChunk", collector.isCanLoadChunk());

        return nbt.getItem();
    }

    public static double getItemValue(ItemStack item){
        return ShopGuiPlusApi.getItemStackPriceSell(item);
    }

    public static List<ItemStack> filterItems(List<ItemStack> drops){
        List<ItemStack> list = new LinkedList<>();
        Iterator<ItemStack> iterator = drops.iterator();

        while(iterator.hasNext()){
            ItemStack item = iterator.next();

            if(getItemValue(item) > 0.0){
                list.add(item);
                iterator.remove();
            }
        }

        return list;
    }

    public static ItemStack liteClone(ItemStack item, ItemMeta meta){
        ItemStack clone = new ItemStack(item.getType(), item.getAmount(), item.getDurability());
        clone.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);

        if(!nbtItem.hasKey("SkyblockCollectorSFSDF"))
            return clone;

        NBTItem nbtClone = new NBTItem(clone);
        nbtClone.setString("SkyblockCollectorSFSDF", nbtItem.getString("SkyblockCollectorSFSDF"));
        return nbtClone.getItem();
    }

}
