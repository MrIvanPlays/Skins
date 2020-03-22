package com.mrivanplays.skins;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.storage.StoredSkin;
import java.util.Optional;
import org.bukkit.entity.Player;
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
    Player player = event.getPlayer();
    Optional<StoredSkin> storedSkinOptional =
        plugin.getSkinStorage().getPlayerSetSkin(player.getUniqueId());
    if (storedSkinOptional.isPresent()) {
      StoredSkin storedSkin = storedSkinOptional.get();
      Skin skin = storedSkin.getSkin();
      plugin.getApi().setSkin(player, skin, storedSkin.getName());
    } else {
      plugin.getApi().setSkin(player, plugin.getApi().getSkin(player.getName()));
    }
  }
}
