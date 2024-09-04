package com.deadshotmdf.SkyblockCollector.GUI;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public interface GUIModel{

    void onOpen(InventoryOpenEvent ev);
    void onClick(InventoryClickEvent ev);
    void onClose(InventoryCloseEvent ev);
    Inventory getInventory();

}
