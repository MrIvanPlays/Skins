package com.mrivanplays.skins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class DefaultSkinSetListener implements Listener {

  private final SkinsBukkitPlugin plugin;

  public DefaultSkinSetListener(SkinsBukkitPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerLoginEvent event) {
    if (event.getResult() != Result.ALLOWED) {
      return;
    }
    plugin
        .getApi()
        .setSkin(event.getPlayer(), plugin.getApi().getSetSkinResponse(event.getPlayer()));
  }
}
