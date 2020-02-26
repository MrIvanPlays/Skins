package com.mrivanplays.skins.protocolsupport;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.SkinsPlugin;
import java.io.File;
import java.util.logging.Logger;

public class SkinsProtocolSupport implements SkinsPlugin {

  private SkinsApi api;

  @Override
  public void enable(File dataFolder, Logger logger) {
    api = new ProtocolSupportSkinsApi(dataFolder, logger);
  }

  @Override
  public SkinsApi getApi() {
    return api;
  }
}
