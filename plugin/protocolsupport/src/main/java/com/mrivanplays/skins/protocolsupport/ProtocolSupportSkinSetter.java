package com.mrivanplays.skins.protocolsupport;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsApi;
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
    MojangResponse response = skinsApi.getSetSkinResponse(profile.getName(), profile.getUUID());
    if (response.getSkin().isPresent()) {
      Skin skin = response.getSkin().get();
      event.addProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
    }
  }
}
