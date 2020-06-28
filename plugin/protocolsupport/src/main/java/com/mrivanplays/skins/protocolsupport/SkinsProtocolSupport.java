package com.mrivanplays.skins.protocolsupport;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.InitializationData;
import com.mrivanplays.skins.core.NoDSSApi;
import com.mrivanplays.skins.core.SkinsPlugin;

public class SkinsProtocolSupport implements SkinsPlugin {

  private SkinsApi api;

  @Override
  public void enable(InitializationData initializationData) {
    api = new NoDSSApi(initializationData);
  }

  @Override
  public SkinsApi getApi() {
    return api;
  }
}
