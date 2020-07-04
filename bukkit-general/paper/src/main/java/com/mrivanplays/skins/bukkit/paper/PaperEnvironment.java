package com.mrivanplays.skins.bukkit.paper;

import com.mrivanplays.skins.api.Environment;

public class PaperEnvironment extends Environment {

  @Override
  public boolean proxy() {
    return false;
  }

  @Override
  public boolean dynamicSkinSetSupported() {
    return true;
  }

  @Override
  public boolean protocolSupport() {
    return false;
  }

  @Override
  public boolean paper() {
    return true;
  }

  @Override
  protected String getCapitalizedName() {
    return "Paper";
  }
}
