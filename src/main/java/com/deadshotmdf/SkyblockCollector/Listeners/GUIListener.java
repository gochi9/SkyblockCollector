package com.deadshotmdf.SkyblockCollector.Listeners;

import com.deadshotmdf.SkyblockCollector.Managers.GUIManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GUIListener implements Listener {

    private final GUIManager guiManager;

    public GUIListener(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent ev) {
        this.guiManager.onOpen(ev);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent ev) {
        this.guiManager.onClick(ev);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent ev) {
        this.guiManager.onClose(ev);
    }

}
