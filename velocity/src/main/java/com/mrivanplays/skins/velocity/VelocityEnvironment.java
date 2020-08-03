package com.mrivanplays.skins.velocity;

import com.mrivanplays.skins.api.Environment;

public class VelocityEnvironment extends Environment {

  @Override
  public boolean proxy() {
    return true;
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
    return "Velocity";
  }
}
