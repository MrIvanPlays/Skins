package com.mrivanplays.skins.bukkit.protocolsupport;

import com.mrivanplays.skins.api.Environment;

public class ProtocolSupportEnvironment extends Environment {

  private final String parentName;
  private final boolean paper;

  public ProtocolSupportEnvironment(String parentName, boolean paper) {
    this.parentName = parentName;
    this.paper = paper;
  }

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
    return true;
  }

  @Override
  public boolean paper() {
    return paper;
  }

  @Override
  protected String getCapitalizedName() {
    return parentName;
  }
}
