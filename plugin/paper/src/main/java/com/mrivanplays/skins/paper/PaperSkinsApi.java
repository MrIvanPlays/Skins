package com.mrivanplays.skins.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.InitializationData;
import org.bukkit.entity.Player;

public class PaperSkinsApi extends AbstractSkinsApi {

  public PaperSkinsApi(InitializationData initializationData) {
    super(initializationData);
  }

  @Override
  protected void setNPCSkin(Player player, Skin skin) {
    PlayerProfile profile = player.getPlayerProfile();
    profile.setProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
    player.setPlayerProfile(profile);
  }
}
