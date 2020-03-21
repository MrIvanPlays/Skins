package com.mrivanplays.skins;

public class Platform {

  public static final PlatformType TYPE = initialize();

  private static PlatformType initialize() {
    try {
      Class.forName("com.destroystokyo.paper.PaperConfig");
      return PlatformType.PAPER;
    } catch (ClassNotFoundException e) {
      try {
        Class.forName("org.spigotmc.SpigotConfig");
        return PlatformType.SPIGOT;
      } catch (ClassNotFoundException e1) {
        return PlatformType.CRAFTBUKKIT;
      }
    }
  }

  public static boolean isPaper() {
    return TYPE == PlatformType.PAPER;
  }

  public enum PlatformType {
    PAPER,
    SPIGOT,
    CRAFTBUKKIT
  }
}
