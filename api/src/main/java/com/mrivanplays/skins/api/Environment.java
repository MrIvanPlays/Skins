package com.mrivanplays.skins.api;

public abstract class Environment {

  public abstract boolean proxy();

  public abstract boolean dynamicSkinSetSupported();

  public abstract boolean protocolSupport();

  public abstract boolean paper();

  public String capitalizedName() {
    return getCapitalizedName() + (protocolSupport() ? " & ProtocolSupport" : "");
  }

  protected abstract String getCapitalizedName();
}
