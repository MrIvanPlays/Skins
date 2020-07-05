package com.mrivanplays.skins.bukkit_general.skull_skinner;

public class SkullSkinnerHandler {

  private static SkullSkinner instance = null;

  public static SkullSkinner getSkullSkinner() {
    if (instance != null) {
      return instance;
    }
    SupportedVersions version = SupportedVersions.getCurrent();
    if (version == null) {
      return null;
    } else {
      if (version == SupportedVersions.v1_12_R1) {
        instance = new SkullSkinner1_12_R1();
      } else if (version == SupportedVersions.v1_13_R2) {
        instance = new SkullSkinner1_13_R2();
      } else if (version == SupportedVersions.v1_14_R1) {
        instance = new SkullSkinner1_14_R1();
      } else if (version == SupportedVersions.v1_15_R1) {
        instance = new SkullSkinner1_15_R1();
      } else {
        instance = new SkullSkinner1_16_R1();
      }
    }
    return instance;
  }
}
