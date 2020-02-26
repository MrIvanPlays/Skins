package com.mrivanplays.skins.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import java.io.File;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public class PaperSkinsApi extends AbstractSkinsApi {

  public PaperSkinsApi(File dataFolder, Logger logger) {
    super(dataFolder, logger);
  }

  @Override
  protected void setNPCSkin(Player player, Skin skin) {
    PlayerProfile profile = player.getPlayerProfile();
    profile.setProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
    player.setPlayerProfile(profile);
  }
}
