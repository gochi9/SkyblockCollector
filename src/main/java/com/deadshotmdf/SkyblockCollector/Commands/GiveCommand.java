package com.deadshotmdf.SkyblockCollector.Commands;

import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import com.deadshotmdf.SkyblockCollector.Utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("skyblockcollector.give")) {
            sender.sendMessage(ConfigSettings.getNoPermission());
            return true;
        }

        ItemStack item = createItemFromArgs(args);
        if (args.length > 0 && !args[0].equalsIgnoreCase(sender.getName()))
            handleItemDistribution(sender, args[0], item);
        else
            giveItemToSender(sender, item);

        return true;
    }

    private void handleItemDistribution(CommandSender sender, String targetName, ItemStack item) {
        if (targetName.equalsIgnoreCase("@a")) {
            distributeItemToAllPlayers(item, sender.getName());
            sender.sendMessage(ConfigSettings.getGiveItemAll());
        }
        else
            giveItemToSpecificPlayer(sender, targetName, item);
    }

    private void distributeItemToAllPlayers(ItemStack item, String sender) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.getInventory().addItem(item);

            if(!player.getName().equals(sender))
                player.sendMessage(ConfigSettings.getReceiveItem(sender));
        }
    }

    private void giveItemToSpecificPlayer(CommandSender sender, String targetName, ItemStack item) {
        Player target = Bukkit.getServer().getPlayer(targetName);

        if(target == null){
            sender.sendMessage(ConfigSettings.getPlayerOffline(targetName));
            return;
        }

        target.getInventory().addItem(item);
        sender.sendMessage(ConfigSettings.getGiveItem(targetName));
        target.sendMessage(ConfigSettings.getReceiveItem(sender.getName()));
    }

    private void giveItemToSender(CommandSender sender, ItemStack item) {
        if (sender instanceof Player) {
            ((Player) sender).getInventory().addItem(item);
            sender.sendMessage(ConfigSettings.getGiveItemSelf());
        }
        else
            sender.sendMessage("Error: Command must be run by a player or specify a target player.");
    }

    private boolean isMax(String[] args) {
        return Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("max"));
    }

    private ItemStack createItemFromArgs(String[] args) {
        boolean max = isMax(args);
        int radius = max ? ConfigSettings.getMaxRadius() : 0;
        double multiplier = max ? ConfigSettings.getMaxSellMultiplier() : 1.0;
        return ItemUtils.createCollectorItem(new Collector(null, 0, 0, radius, multiplier, max, null));
    }
}
