package com.mrivanplays.skins.protocolsupport;

import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.io.File;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;

public class ProtocolSupportSkinsApi extends AbstractSkinsApi {

  public ProtocolSupportSkinsApi(
      File dataFolder, Logger logger, Function<SkullItemBuilderData, ItemStack> transformer) {
    super(dataFolder, logger, transformer);
  }
}
