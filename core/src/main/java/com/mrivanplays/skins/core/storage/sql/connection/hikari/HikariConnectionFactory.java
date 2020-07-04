package com.mrivanplays.skins.core.storage.sql.connection.hikari;

import com.google.common.collect.ImmutableList;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.storage.sql.connection.SQLConnectionFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Credits: lucko/LuckPerms
 * @author lucko
 */
public abstract class HikariConnectionFactory implements SQLConnectionFactory {

  protected final SkinsConfiguration configuration;
  protected final SkinsPlugin plugin;
  private HikariDataSource hikari;

  public HikariConnectionFactory(SkinsPlugin plugin) {
    this.configuration = plugin.getConfiguration();
    this.plugin = plugin;
  }

  protected String getDriverClass() {
    return null;
  }

  protected void appendProperties(HikariConfig config, Map<String, String> properties) {
    properties.forEach(config::addDataSourceProperty);
  }

  protected void appendConfigInfo(HikariConfig config) {
    config.setDataSourceClassName(getDriverClass());
    config.addDataSourceProperty("serverName", configuration.getStorageCredentials().getAddress());
    config.addDataSourceProperty("port", configuration.getStorageCredentials().getPort());
    config.addDataSourceProperty(
        "databaseName", configuration.getStorageCredentials().getDatabase());
    config.setUsername(configuration.getStorageCredentials().getUsername());
    config.setPassword(configuration.getStorageCredentials().getPassword());
  }

  @Override
  public void connect() throws SQLException {
    HikariConfig config;
    try {
      config = new HikariConfig();
    } catch (LinkageError e) {
      handleLinkageError(e);
      throw e;
    }

    config.setPoolName("skins-hikari");
    appendConfigInfo(config);

    Map<String, String> properties = new HashMap<>();
    properties.put("useUnicode", "true");
    properties.put("characterEncoding", "utf8");
    properties.put("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

    appendProperties(config, properties);
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(10);
    config.setMaxLifetime(1800000);
    config.setConnectionTimeout(5000);
    config.setInitializationFailTimeout(-1);

    this.hikari = new HikariDataSource(config);
  }

  @Override
  public Connection getConnection() throws SQLException {
    if (hikari == null) {
      throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
    }
    Connection connection = hikari.getConnection();
    if (connection == null) {
      throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
    }
    return connection;
  }

  @Override
  public void close() throws SQLException {
    if (hikari != null) {
      hikari.close();
    }
  }

  private void handleLinkageError(LinkageError linkageError) {
    List<String> noteworthyClasses =
        ImmutableList.of(
            "org.slf4j.LoggerFactory",
            "org.slf4j.ILoggerFactory",
            "org.apache.logging.slf4j.Log4jLoggerFactory",
            "org.apache.logging.log4j.spi.LoggerContext",
            "org.apache.logging.log4j.spi.AbstractLoggerAdapter",
            "org.slf4j.impl.StaticLoggerBinder");

    Logger logger = plugin.getLogger();
    logger.warning(
        "A "
            + linkageError.getClass().getSimpleName()
            + " has occurred whilst initialising Hikari. This is likely due to classloading conflicts between other plugins.");
    logger.warning(
        "Please check for other plugins below (and try loading Skins without them installed) before reporting the issue.");

    for (String className : noteworthyClasses) {
      Class<?> clazz;
      try {
        clazz = Class.forName(className);
      } catch (Exception ex) {
        continue;
      }

      ClassLoader loader = clazz.getClassLoader();
      String loaderName;
      try {
        loaderName = plugin.identifyClassLoader(loader) + " (" + loader.toString() + ")";
      } catch (Exception e) {
        loaderName = loader.toString();
      }

      logger.warning("Class " + className + " has been loaded by: " + loaderName);
    }
  }
}
