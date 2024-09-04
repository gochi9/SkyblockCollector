package com.deadshotmdf.SkyblockCollector.Managers;

import com.deadshotmdf.SkyblockCollector.Config.ConfigSettings;
import com.deadshotmdf.SkyblockCollector.GUI.CollectorUpgradeGUI;
import com.deadshotmdf.SkyblockCollector.GUI.GUIModel;
import com.deadshotmdf.SkyblockCollector.Objects.Collector;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

//Currently only has one single extra GUI
//However, I tried to make it easy to create and add more custom GUIs for future updates
public class GUIManager{

    private final HashMap<Inventory, GUIModel> inventoryMap;

    public GUIManager(){
        this.inventoryMap = new HashMap<>();
    }

    /**
     * Clears all registered inventories and their associated GUIs.
     * Only used for configuration reload
     */
    public void reload(){
        this.inventoryMap.clear();
    }

    /**
     * Registers and initializes a GUI for a given collector.
     *
     * @param collector The collector for which the GUI is being created.
     * @param manager   The manager handling the collector's data and operations.
     */
    public void registerCollectorInventory(Collector collector, CollectorManager manager){
        // Remove any existing GUI from the collector.
        collector.removeGUI();

        // Create a new inventory with specified size and name from config settings.
        Inventory inv = Bukkit.createInventory(null, ConfigSettings.getGUISize(), ConfigSettings.getGUIName(collector));

        // Create and register a new GUI model.
        CollectorUpgradeGUI gui = new CollectorUpgradeGUI(inv, collector, manager);
        inventoryMap.put(inv, gui);

        // Associate the newly created GUI with the collector.
        collector.setGui(gui);
    }

    //Open and close event aren't used for anything at the moment, but this implementation might come in useful for future updates.
    public void onOpen(InventoryOpenEvent ev){
        GUIModel gui = inventoryMap.get(ev.getInventory());

        if(gui != null)
            gui.onOpen(ev);
    }

    public void onClick(InventoryClickEvent ev){
        GUIModel gui = inventoryMap.get(ev.getInventory());

        if(gui != null)
            gui.onClick(ev);
    }

    public void onClose(InventoryCloseEvent ev){
        GUIModel gui = inventoryMap.get(ev.getInventory());

        if(gui != null)
            gui.onClose(ev);
    }

}
