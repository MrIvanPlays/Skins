package com.mrivanplays.skins.bukkit_general.skull_skinner;

import org.bukkit.Bukkit;

public enum SupportedVersions {
  v1_12_R1,
  v1_13_R2,
  v1_14_R1,
  v1_15_R1,
  v1_16_R1;

  private static final String nmsVersionString =
      Bukkit.getServer().getClass().getName().replace(".", ",").split(",")[3];

  public static SupportedVersions getCurrent() {
    if (isCurrentSupported()) {
      return SupportedVersions.valueOf(nmsVersionString);
    } else {
      return null;
    }
  }

  public static boolean isCurrentSupported() {
    try {
      SupportedVersions.valueOf(nmsVersionString);
      return true;
    } catch (Throwable e) {
      return false;
    }
  }
}
