package com.mrivanplays.skins.api;

import java.util.Optional;

/** Represents provider of the {@link SkinsApi} */
public class SkinsApiProvider {

  private static SkinsApi api;

  /**
   * Returns the api. May return null if not initialized.
   *
   * @return api
   */
  public static SkinsApi get() {
    return api;
  }

  /**
   * Returns the api safely wrapped in a {@link Optional}
   *
   * @return optional of skins api
   */
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
