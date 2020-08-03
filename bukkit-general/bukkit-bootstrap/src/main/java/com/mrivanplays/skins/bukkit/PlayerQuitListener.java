package com.mrivanplays.skins.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

  private final BukkitSkinsPlugin plugin;

  public PlayerQuitListener(BukkitSkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    plugin.removeFromUserMap(event.getPlayer().getUniqueId());
  }
}
