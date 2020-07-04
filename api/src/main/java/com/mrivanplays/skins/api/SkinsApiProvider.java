package com.mrivanplays.skins.api;

import java.util.Optional;

public class SkinsApiProvider {

  private static SkinsApi api;

  public static SkinsApi get() {
    return api;
  }

  public static Optional<SkinsApi> safeGet() {
    return Optional.ofNullable(api);
  }

  public static void set(SkinsApi newApi) {
    // safety first :)
    if (SkinsApiProvider.api != null) {
      throw new IllegalArgumentException("Api already set.");
    }
    if (newApi == null) {
      throw new NullPointerException("newApi");
    }
    SkinsApiProvider.api = newApi;
  }
}
