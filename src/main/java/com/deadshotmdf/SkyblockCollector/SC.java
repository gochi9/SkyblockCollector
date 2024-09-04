package com.deadshotmdf.SkyblockCollector;

import com.deadshotmdf.SkyblockCollector.Commands.GiveCommand;
import com.deadshotmdf.SkyblockCollector.Commands.ReloadCommand;
import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.Listeners.*;
import com.deadshotmdf.SkyblockCollector.Managers.CollectorManager;
import com.deadshotmdf.SkyblockCollector.Managers.GUIManager;
import com.deadshotmdf.SkyblockCollector.Managers.SkyblockManager;
import com.deadshotmdf.SkyblockCollector.Storage.CollectorDaoFactory;
import com.deadshotmdf.SkyblockCollector.Tasks.AutoSellTask;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SC extends JavaPlugin {

    private CollectorDaoFactory collectorDaoFactory;
    private GUIManager guiManager;
    private CollectorManager collectorManager;
    private SkyblockManager skyblockManager;
    private HolographicDisplaysAPI hologramAPI;
    private Integer taskID;

    private static Economy economy;

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Init
        ConfigSettings.reloadConfig(this);
        this.hologramAPI = HolographicDisplaysAPI.get(this);
        this.collectorDaoFactory = new CollectorDaoFactory(this.getConfig(), this.getDataFolder().getAbsolutePath());
        this.guiManager = new GUIManager();
        this.skyblockManager = new SkyblockManager();
        this.collectorManager = new CollectorManager(collectorDaoFactory.getStorage(), hologramAPI, guiManager, skyblockManager);

        //Registering events
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockBreak(collectorManager), this);
        pm.registerEvents(new CancelChunkUnload(collectorManager), this);
        pm.registerEvents(new CollectorPlace(collectorManager), this);
        pm.registerEvents(new CatchItem(collectorManager), this);
        pm.registerEvents(new CancelInteract(collectorManager, skyblockManager), this);
        pm.registerEvents(new GUIListener(guiManager), this);

        if(pm.isPluginEnabled("SuperiorSkyblock2"))
            pm.registerEvents(new SkyblockListeners(skyblockManager, collectorManager), this);

        //Registering commands
        this.getCommand("collectorgive").setExecutor(new GiveCommand());
        this.getCommand("collectorreload").setExecutor(new ReloadCommand(this, collectorManager));

        //this.getCommand("collectorstresstest").setExecutor(new StressTest(this, collectorManager));

        //Start autoSell task
        restartTask();
    }

    public void onDisable(){
        //Prevents players from taking items from custom GUIs if the /reload command is ran for whatever reason
        for(Player player : Bukkit.getOnlinePlayers())
            player.closeInventory();

        this.collectorManager.saveAll();
    }

    public void restartTask(){
        if(taskID != null)
            Bukkit.getScheduler().cancelTask(taskID);

        taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoSellTask(this, collectorManager), 20, ConfigSettings.getAutoSellTaskDelay()).getTaskId();
    }

    //Vault economy
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    //Getters
    public static Economy getEconomy() {
        return economy;
    }

    public CollectorDaoFactory getCollectorDaoFactory(){
        return collectorDaoFactory;
    }

    public GUIManager getGuiManager(){
        return guiManager;
    }

    public CollectorManager getCollectorManager(){
        return collectorManager;
    }

    public SkyblockManager getSkyblockManager(){
        return skyblockManager;
    }

    public HolographicDisplaysAPI getHologramAPI(){
        return hologramAPI;
    }

}
