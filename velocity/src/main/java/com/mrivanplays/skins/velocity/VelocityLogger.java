package com.mrivanplays.skins.velocity;

import org.slf4j.Logger;

public class VelocityLogger implements com.mrivanplays.skins.core.Logger {

  private final Logger parent;

  public VelocityLogger(Logger parent) {
    this.parent = parent;
  }

  @Override
  public void info(String message) {
    parent.info(message);
  }

  @Override
  public void warning(String message) {
    parent.warn(message);
  }

  @Override
  public void severe(String message) {
    parent.error(message);
  }
}
