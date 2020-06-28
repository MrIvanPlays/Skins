package com.mrivanplays.skins.api;

/** Represents a version info, providing information about the plugin's exact versioning. */
public final class SkinsVersionInfo {

  private final String version;
  private final String commit;
  private final int buildNumber;

  public SkinsVersionInfo(String version, String commit, int buildNumber) {
    this.version = version;
    this.commit = commit;
    this.buildNumber = buildNumber;
  }

  /**
   * Returns the version specified in plugin.yml
   *
   * @return version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Returns whenever the plugin build, ran by the server, is a development build.
   *
   * @return <code>true</code> if dev build, <code>false</code> otherwise
   */
  public boolean isDevBuild() {
    return version.contains("SNAPSHOT");
  }

  /**
   * Returns the commit which triggered the build of the ran instance of the plugin
   *
   * @return commit
   */
  public String getCommit() {
    return commit;
  }

  /**
   * Returns the build number of the ran plugin
   *
   * @return build number
   */
  public int getBuildNumber() {
    return buildNumber;
  }
}
