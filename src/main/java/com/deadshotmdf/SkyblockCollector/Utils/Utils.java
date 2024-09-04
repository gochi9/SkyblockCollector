package com.deadshotmdf.SkyblockCollector.Utils;

import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.SC;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class Utils {

    public static String locationToString(Location loc){
        return loc.getWorld().getUID() + "#" + loc.getBlockX() + "#" + loc.getBlockY() + "#" + loc.getBlockZ();
    }

    public static Location locationFromString(String loc){
        if(loc == null)
            return null;

        String[] parts = loc.split("#");

        if(parts.length != 4)
            return null;

        UUID w = getUUID(parts[0]);
        World world;

        if(w == null || (world = Bukkit.getWorld(w)) == null)
            return null;

        Integer x = getInteger(parts[1]);
        Integer y = getInteger(parts[2]);
        Integer z = getInteger(parts[3]);

        if(x == null || y == null || z == null)
            return null;

        return new Location(world, x, y, z);
    }

    public static UUID getUUID(String s){
        try{
            return UUID.fromString(s);
        }
        catch(Exception e){
            return null;
        }
    }

    public static Integer getInteger(String s){
        try{
            return Integer.parseInt(s);
        }
        catch(Throwable e){
            return null;
        }
    }

    public static Integer getInteger(String s, int def){
        try{
            int i = Integer.parseInt(s);
            return Math.max(i, def);
        }
        catch(Throwable e){
            return def;
        }
    }

    public static Material getMaterial(String s, Material def){
        try{
            Material mat = Material.getMaterial(s);

            return mat != null ? mat : def;
        }
        catch(Throwable e){
            return def;
        }
    }

    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static String formatDouble(Double value){
        if(value == null)
            return "0";

        StringBuilder builder = new StringBuilder();

        if(isWhole(value))
            return builder.append(((int)value.doubleValue())).toString();

        if(value > -1.0 && value < 1)
            builder.append("0");

        return builder.append(df.format(value)).toString();
    }

    private static boolean isWhole(double value) {
        return value == Math.floor(value) && !Double.isInfinite(value);
    }

    public static boolean deductPayment(Player player, double cost){
        Economy eco = SC.getEconomy();

        double balance = eco.getBalance(player);

        if(cost > balance){
            player.sendMessage(ConfigSettings.getTooPoor(cost, balance));
            return false;
        }

        eco.withdrawPlayer(player, cost);
        player.sendMessage(ConfigSettings.getUpgradePurchased(cost, eco.getBalance(player)));
        return true;
    }

}
