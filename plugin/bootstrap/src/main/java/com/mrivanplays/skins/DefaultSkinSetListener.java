package com.mrivanplays.skins;

import com.mrivanplays.skins.bukkit.abstraction.SupportedVersions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class DefaultSkinSetListener implements Listener {

  private final SkinsBukkitPlugin plugin;

  public DefaultSkinSetListener(SkinsBukkitPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (SupportedVersions.getCurrent().getProtocolVersion()
        < SupportedVersions.v1_16_R1.getProtocolVersion()) {
      plugin
          .getApi()
          .setSkin(event.getPlayer(), plugin.getApi().getSetSkinResponse(event.getPlayer()));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerLogin(PlayerLoginEvent event) {
    if (SupportedVersions.getCurrent().getProtocolVersion()
        >= SupportedVersions.v1_16_R1.getProtocolVersion()) {
      plugin
          .getApi()
          .setSkin(event.getPlayer(), plugin.getApi().getSetSkinResponse(event.getPlayer()));
    }
  }
}
