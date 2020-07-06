package com.mrivanplays.skins.bukkit.abstraction;

import org.bukkit.Bukkit;

public enum SupportedVersions {
  v1_12_R1(340),
  v1_13_R2(404),
  v1_14_R1(477),
  v1_15_R1(573),
  v1_16_R1(736);

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

  private int protocolVersion;

  SupportedVersions(int protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  public int getProtocolVersion() {
    return protocolVersion;
  }
}
