package com.mrivanplays.skins.paper;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.InitializationData;
import com.mrivanplays.skins.core.SkinsPlugin;

public class SkinsPaper implements SkinsPlugin {

  private PaperSkinsApi api;

  @Override
  public void enable(InitializationData initializationData) {
    api = new PaperSkinsApi(initializationData);
  }

  @Override
  public SkinsApi getApi() {
    return api;
  }
}
