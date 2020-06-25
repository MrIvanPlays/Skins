package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.Skin;
import org.bukkit.entity.Player;

public class NoDSSApi extends AbstractSkinsApi {

  public NoDSSApi(InitializationData initializationData) {
    super(initializationData);
  }

  @Override
  protected void setNPCSkin(Player player, Skin skin) {}
}
