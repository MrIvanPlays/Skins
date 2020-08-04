package com.mrivanplays.skins.core;

/**
 * OH GOD WHY: because different frameworks use different logging bukkit uses java's logger, while
 * velocity uses slf4j and that's why we need that.
 */
public interface Logger {

  void info(String message);

  void warning(String message);

  void severe(String message);
}
