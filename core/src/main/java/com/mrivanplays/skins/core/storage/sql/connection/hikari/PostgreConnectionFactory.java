package com.mrivanplays.skins.core.storage.sql.connection.hikari;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.zaxxer.hikari.HikariConfig;
import java.util.Map;
import java.util.function.Function;

public class PostgreConnectionFactory extends HikariConnectionFactory {

  public PostgreConnectionFactory(SkinsPlugin plugin) {
    super(plugin);
  }

  @Override
  protected void appendProperties(HikariConfig config, Map<String, String> properties) {
    properties.remove("useUnicode");
    properties.remove("characterEncoding");

    super.appendProperties(config, properties);
  }

  @Override
  protected void appendConfigInfo(HikariConfig config) {
    config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
    config.addDataSourceProperty("serverName", configuration.getStorageCredentials().getAddress());
    config.addDataSourceProperty("portNumber", configuration.getStorageCredentials().getPort());
    config.addDataSourceProperty(
        "databaseName", configuration.getStorageCredentials().getDatabase());
    config.addDataSourceProperty("user", configuration.getStorageCredentials().getUsername());
    config.addDataSourceProperty("password", configuration.getStorageCredentials().getPassword());
  }

  @Override
  public Function<String, String> statementTickReplacer() {
    return s -> s.replace("'", "\"");
  }
}
