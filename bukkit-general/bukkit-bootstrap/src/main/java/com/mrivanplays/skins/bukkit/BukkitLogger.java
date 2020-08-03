package com.mrivanplays.skins.bukkit;

import java.util.logging.Logger;

public class BukkitLogger implements com.mrivanplays.skins.core.Logger {

  private final Logger parent;

  public BukkitLogger(Logger parent) {
    this.parent = parent;
  }

  @Override
  public void info(String message) {
    parent.info(message);
  }

  @Override
  public void warning(String message) {
    parent.warning(message);
  }

  @Override
  public void severe(String message) {
    parent.severe(message);
  }
}
