package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkinsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DefaultSkinSetListener implements Listener {

  private final SkinsPlugin plugin;

  public DefaultSkinSetListener(SkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    SkinsUser user = plugin.obtainUser(player.getName());
    user.getSkin().join().ifPresent(user::setSkin);
  }
}
