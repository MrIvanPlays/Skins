package com.mrivanplays.skins.api;

import org.jetbrains.annotations.NotNull;

/** Represents platform. */
public enum Platform {
  CRAFTBUKKIT("CraftBukkit"),
  CRAFTBUKKIT_WITH_PROTOCOLSUPPORT("CraftBukkit & ProtocolSupport"),
  SPIGOT("Spigot"),
  SPIGOT_WITH_PROTOCOLSUPPORT("Spigot & ProtocolSupport"),
  PAPER("Paper"),
  PAPER_WITH_PROTOCOLSUPPORT("Paper & ProtocolSupport"),
  VELOCITY("Velocity");

  private final String capitalizedName;

  Platform(String capitalizedName) {
    this.capitalizedName = capitalizedName;
  }

  /**
   * Returns the capitalized name of the platform type.
   *
   * @return capitalized name.
   */
  @NotNull
  public String getCapitalizedName() {
    return capitalizedName;
  }

  /**
   * Returns whether or not the platform supports dynamic skin set.
   *
   * @return if dynamic skin set is supported
   */
  public boolean dynamicSkinSetSupported() {
    return this == PAPER;
  }

  /**
   * Returns whether or not the plugin ProtocolSupport is being present in the platform we're being
   * ran on.
   *
   * @return if ProtocolSupport is present
   */
  public boolean protocolSupportPresent() {
    switch (this) {
      case PAPER_WITH_PROTOCOLSUPPORT:
      case SPIGOT_WITH_PROTOCOLSUPPORT:
      case CRAFTBUKKIT_WITH_PROTOCOLSUPPORT:
        return true;
      default:
        return false;
    }
  }

  /**
   * Returns whether or not the plugin is ran on proxy.
   *
   * @return if ran on proxy
   */
  public boolean proxy() {
    return this == VELOCITY;
  }
}
