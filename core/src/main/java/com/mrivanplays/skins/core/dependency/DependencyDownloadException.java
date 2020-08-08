package com.mrivanplays.skins.core.dependency;

public class DependencyDownloadException extends Exception {

  public DependencyDownloadException() {}

  public DependencyDownloadException(String message) {
    super(message);
  }

  public DependencyDownloadException(String message, Throwable cause) {
    super(message, cause);
  }

  public DependencyDownloadException(Throwable cause) {
    super(cause);
  }
}
