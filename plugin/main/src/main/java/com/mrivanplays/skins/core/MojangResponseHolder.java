package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.MojangResponse;

public class MojangResponseHolder {

  private final MojangResponse response;
  private final boolean justFetched;

  public MojangResponseHolder(MojangResponse response, boolean justFetched) {
    this.response = response;
    this.justFetched = justFetched;
  }

  public MojangResponse getResponse() {
    return response;
  }

  public boolean isJustFetched() {
    return justFetched;
  }
}
