package com.deadshotmdf.SkyblockCollector.Commands;

import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.SC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private SC main;
    private CollectorManager collectorManager;

    public ReloadCommand(SC main, CollectorManager collectorManager) {
        this.main = main;
        this.collectorManager = collectorManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && !sender.hasPermission("skyblockcollector.reload")) {
            sender.sendMessage(ConfigSettings.getNoPermission());
            return true;
        }

        ConfigSettings.reloadConfig(main);
        collectorManager.reloadConfig();
        main.restartTask();
        sender.sendMessage(ConfigSettings.getReloadConfig());

        return true;
    }
}
