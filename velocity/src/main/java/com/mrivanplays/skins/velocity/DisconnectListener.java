package com.mrivanplays.skins.velocity;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.connection.DisconnectEvent;

public class DisconnectListener implements EventHandler<DisconnectEvent> {

  public static void register(VelocityPlugin bootstrap, VelocitySkinsPlugin plugin) {
    DisconnectListener listener = new DisconnectListener(plugin);
    bootstrap.getProxy().getEventManager().register(bootstrap, DisconnectEvent.class, listener);
  }

  private final VelocitySkinsPlugin plugin;

  public DisconnectListener(VelocitySkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void execute(DisconnectEvent event) {
    plugin.removeFromUserCache(event.getPlayer().getUniqueId());
  }
}
