package com.mrivanplays.skins.protocolsupport;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.io.File;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;

public class SkinsProtocolSupport implements SkinsPlugin {

  private SkinsApi api;

  @Override
  public void enable(
      File dataFolder, Logger logger, Function<SkullItemBuilderData, ItemStack> transformer) {
    api = new ProtocolSupportSkinsApi(dataFolder, logger, transformer);
  }

  @Override
  public SkinsApi getApi() {
    return api;
  }
}
