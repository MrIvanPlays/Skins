package com.mrivanplays.skins.protocolsupport;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.storage.StoredSkin;
import java.util.Optional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import protocolsupport.api.events.PlayerProfileCompleteEvent;
import protocolsupport.api.utils.Profile;
import protocolsupport.api.utils.ProfileProperty;

public class ProtocolSupportSkinSetter implements Listener {

  private final AbstractSkinsApi skinsApi;

  public ProtocolSupportSkinSetter(AbstractSkinsApi skinsApi) {
    this.skinsApi = skinsApi;
  }

  @EventHandler
  public void on(PlayerProfileCompleteEvent event) {
    Profile profile = event.getConnection().getProfile();
    Optional<StoredSkin> storedSkinOptional =
        skinsApi.getSkinStorage().getPlayerSetSkin(profile.getUUID());
    if (storedSkinOptional.isPresent()) {
      StoredSkin storedSkin = storedSkinOptional.get();
      Skin skin = storedSkin.getSkin();
      event.addProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
    } else {
      MojangResponse response =
          skinsApi.getSkinFetcher().getSkin(profile.getName(), profile.getUUID());
      Optional<Skin> skinOptional = response.getSkin();
      if (!skinOptional.isPresent()) {
        return;
      }
      Skin skin = skinOptional.get();
      skinsApi.modifyStoredSkin(profile.getUUID(), skin, profile.getName());
      event.addProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
    }
  }
}
