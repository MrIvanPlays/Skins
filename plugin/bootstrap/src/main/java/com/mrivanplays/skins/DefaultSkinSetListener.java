package com.mrivanplays.skins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DefaultSkinSetListener implements Listener {

  private final SkinsBukkitPlugin plugin;

  public DefaultSkinSetListener(SkinsBukkitPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void on(PlayerJoinEvent event) {
    plugin
        .getApi()
        .setSkin(event.getPlayer(), plugin.getApi().getSetSkinResponse(event.getPlayer()));
  }
}
