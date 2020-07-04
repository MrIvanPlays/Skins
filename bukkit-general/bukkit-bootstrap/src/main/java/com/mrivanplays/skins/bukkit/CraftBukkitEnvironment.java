package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Environment;

public class CraftBukkitEnvironment extends Environment {

  @Override
  public boolean proxy() {
    return false;
  }

  @Override
  public boolean dynamicSkinSetSupported() {
    return false;
  }

  @Override
  public boolean protocolSupport() {
    return false;
  }

  @Override
  public boolean paper() {
    return false;
  }

  @Override
  protected String getCapitalizedName() {
    return "CraftBukkit";
  }
}
