package com.mrivanplays.skins.bukkit.communicator;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

  private final SkinsBukkitCommunicator plugin;

  public PlayerJoinListener(SkinsBukkitCommunicator plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    plugin
        .getServer()
        .getScheduler()
        .scheduleSyncDelayedTask(
            plugin,
            () -> {
              ByteArrayDataOutput out = ByteStreams.newDataOutput();
              out.writeUTF("Hello");
              out.writeUTF(event.getPlayer().getUniqueId().toString());
              event.getPlayer().sendPluginMessage(plugin, "skins:plugin", out.toByteArray());
            },
            20);
  }
}
