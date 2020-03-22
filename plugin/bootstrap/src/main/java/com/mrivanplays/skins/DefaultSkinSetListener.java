package com.mrivanplays.skins;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.MojangResponseHolder;
import com.mrivanplays.skins.core.StoredSkin;
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
      plugin
          .getApi()
          .setSkin(player, checkForSkinUpdate(storedSkin.getName(), skin), storedSkin.getName());
    } else {
      MojangResponseHolder responseHolder = plugin.getApi().getSkinHolder(player.getName());
      if (responseHolder.isJustFetched()) {
        plugin.getApi().setSkin(player, responseHolder.getResponse());
      } else {
        MojangResponse response = responseHolder.getResponse();
        Optional<Skin> skinOptional = response.getSkin();
        if (!skinOptional.isPresent()) {
          return;
        }
        plugin
            .getApi()
            .setSkin(
                player, checkForSkinUpdate(player.getName(), skinOptional.get()), player.getName());
      }
    }
  }

  private Skin checkForSkinUpdate(String name, Skin skin) {
    MojangResponse response = plugin.getSkinFetcher().apiFetch(name, skin.getOwner()).join();
    if (response.getSkin().isPresent()) {
      Skin fetched = response.getSkin().get();
      if (skin.getTexture().equalsIgnoreCase(fetched.getTexture())) {
        return skin;
      } else {
        return fetched;
      }
    } else {
      return skin;
    }
  }
}
