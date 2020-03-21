package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.io.File;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;

public class SkinsBukkit implements SkinsPlugin {

  private SkinsApi skinsApi;

  @Override
  public void enable(
      File dataFolder, Logger logger, Function<SkullItemBuilderData, ItemStack> transformer) {
    skinsApi = new BukkitSkinsApi(dataFolder, logger, transformer);
  }

  @Override
  public SkinsApi getApi() {
    return skinsApi;
  }
}
