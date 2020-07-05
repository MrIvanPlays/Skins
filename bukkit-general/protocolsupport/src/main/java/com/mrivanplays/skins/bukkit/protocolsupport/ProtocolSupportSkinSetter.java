package com.mrivanplays.skins.bukkit.protocolsupport;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkinsUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import protocolsupport.api.Connection;
import protocolsupport.api.events.PlayerProfileCompleteEvent;
import protocolsupport.api.utils.ProfileProperty;

public class ProtocolSupportSkinSetter implements Listener {

  private final SkinsPlugin plugin;

  public ProtocolSupportSkinSetter(SkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void on(PlayerProfileCompleteEvent event) {
    Connection connection = event.getConnection();
    SkinsUser user = plugin.obtainUser(connection.getProfile().getName());
    user.getSkin()
        .thenApplyAsync(skin -> skin.orElse(null), plugin.getScheduler().async())
        .thenAcceptAsync(skin -> {
          if (skin != null) {
            event.addProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
          }
        }, plugin.getScheduler().sync());
  }
}
