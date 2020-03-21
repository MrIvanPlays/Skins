package com.mrivanplays.skins.paper;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.io.File;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;

public class SkinsPaper implements SkinsPlugin {

  private PaperSkinsApi api;

  @Override
  public void enable(
      File dataFolder, Logger logger, Function<SkullItemBuilderData, ItemStack> transformer) {
    api = new PaperSkinsApi(dataFolder, logger, transformer);
  }

  @Override
  public SkinsApi getApi() {
    return api;
  }
}
