package com.mrivanplays.skins.api;

/** Represents the environment the plugin is being on. */
public abstract class Environment {

  /**
   * Returns whether or not the plugin is ran on a proxy (Velocity or BungeeCord)
   *
   * @return proxy
   */
  public abstract boolean proxy();

  /**
   * Returns whether or not dynamic skin set is supported.
   *
   * @return support of dss
   */
  public abstract boolean dynamicSkinSetSupported();

  /**
   * Returns whether or not on the platform we're running is running the plugin ProtocolSupport.
   *
   * @return ProtocolSupport present or not
   */
  public abstract boolean protocolSupport();

  /**
   * Returns whether or not the platform we're running on is Paper (famous fork of Spigot)
   *
   * @return platform paper
   */
  public abstract boolean paper();

  /**
   * Returns the capitalized name of the environment we're running on.
   *
   * @return capitalized name
   */
  public String capitalizedName() {
    return getCapitalizedName() + (protocolSupport() ? " & ProtocolSupport" : "");
  }

  protected abstract String getCapitalizedName();
}
