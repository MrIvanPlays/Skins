package com.mrivanplays.skins.core.util;

import com.mrivanplays.skins.api.Environment;
import com.mrivanplays.skins.api.SkinsInfo;
import com.mrivanplays.skins.core.Logger;

public class SkinsInfoParser {

  public static SkinsInfo parseInfo(
      String version, String implementationVersion, Logger logger, Environment environment) {
    String[] implVersionSplit = implementationVersion.split(":");
    String commit = implVersionSplit[3];
    String buildNumberPart = implVersionSplit[4];
    int buildNumber;
    try {
      buildNumber = Integer.parseInt(buildNumberPart);
    } catch (NumberFormatException e) {
      logger.warning("Could not detect proper build number, custom build?");
      buildNumber = -1;
    }
    return new SkinsInfo(version, commit, buildNumber, environment);
  }
}
