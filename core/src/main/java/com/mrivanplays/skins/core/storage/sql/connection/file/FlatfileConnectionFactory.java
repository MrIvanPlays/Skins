package com.mrivanplays.skins.core.storage.sql.connection.file;

import com.mrivanplays.skins.core.storage.sql.connection.SQLConnectionFactory;
import java.nio.file.Path;

abstract class FlatfileConnectionFactory implements SQLConnectionFactory {

  protected final Path file;

  FlatfileConnectionFactory(Path file) {
    this.file = file;
  }

  protected Path getWriteFile() {
    return file;
  }

  @Override
  public void connect() {}
}
