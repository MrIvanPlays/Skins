package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.handle.SkinSetterHandler;
import com.mrivanplays.skins.core.SkinsPlugin;
import java.io.File;
import java.util.logging.Logger;

public class SkinsBukkit implements SkinsPlugin {

  private SkinSetter skinSetter;
  private SkinsApi skinsApi;

  @Override
  public void enable(File dataFolder, Logger logger) {
    skinSetter = SkinSetterHandler.getSkinSetter();
    skinsApi = new BukkitSkinsApi(this, dataFolder, logger);
  }

  @Override
  public SkinsApi getApi() {
    return skinsApi;
  }

  public SkinSetter getSkinSetter() {
    return skinSetter;
  }
}
