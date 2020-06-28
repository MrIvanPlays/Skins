package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.InitializationData;
import com.mrivanplays.skins.core.SkinsPlugin;

public class SkinsBukkit implements SkinsPlugin {

  private SkinsApi skinsApi;

  @Override
  public void enable(InitializationData initializationData) {
    skinsApi = new BukkitSkinsApi(initializationData);
  }

  @Override
  public SkinsApi getApi() {
    return skinsApi;
  }
}
