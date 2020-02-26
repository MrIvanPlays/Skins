package com.mrivanplays.skins.paper;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.SkinsPlugin;
import java.io.File;
import java.util.logging.Logger;

public class SkinsPaper implements SkinsPlugin {

  private PaperSkinsApi api;

  @Override
  public void enable(File dataFolder, Logger logger) {
    api = new PaperSkinsApi(dataFolder, logger);
  }

  @Override
  public SkinsApi getApi() {
    return api;
  }
}
