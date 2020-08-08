package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.SkinsApiProvider;
import com.mrivanplays.skins.bukkit_general.skull_skinner.SupportedVersions;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsApiImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class DefaultSkinSetListener implements Listener {

  private final BukkitSkinsPlugin plugin;
  private final SupportedVersions current;

  public DefaultSkinSetListener(BukkitSkinsPlugin plugin) {
    this.plugin = plugin;
    this.current = SupportedVersions.getCurrent();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(PlayerJoinEvent event) {
    if (current.getProtocolVersion() < SupportedVersions.v1_16_R1.getProtocolVersion()) {
      setSkin(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLogin(PlayerLoginEvent event) {
    if (current.getProtocolVersion() >= SupportedVersions.v1_16_R1.getProtocolVersion()) {
      setSkin(event.getPlayer());
    }
  }

  private void setSkin(Player player) {
    AbstractSkinsUser user = plugin.obtainUser(player);
    user.getSkin()
        .thenAccept(
            skin ->
                skin.ifPresent(
                    skinObj -> {
                      SkinsApiImpl api = (SkinsApiImpl) SkinsApiProvider.get();
                      user.setSkin(
                          skinObj,
                          api.getSkinAccessor().getDataProvider().retrieveName(skinObj.getOwner()),
                          false);
                    }));
  }
}
