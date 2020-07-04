package com.mrivanplays.skins.core.storage;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.config.SkinsConfiguration;
import com.mrivanplays.skins.core.storage.sql.SqlStorageProvider;
import com.mrivanplays.skins.core.storage.sql.connection.file.H2ConnectionFactory;
import com.mrivanplays.skins.core.storage.sql.connection.file.SQLiteConnectionFactory;
import com.mrivanplays.skins.core.storage.sql.connection.hikari.MariaDbConnectionFactory;
import com.mrivanplays.skins.core.storage.sql.connection.hikari.MySqlConnectionFactory;
import com.mrivanplays.skins.core.storage.sql.connection.hikari.PostgreConnectionFactory;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Storage {

  private StorageMigration migration;

  private final SkinsConfiguration configuration;
  private final SkinsPlugin plugin;
  private StorageProvider storageProvider;

  public Storage(SkinsPlugin plugin) {
    this.configuration = plugin.loadAndGetConfiguration();
    this.plugin = plugin;
  }

  public Storage(SkinsPlugin plugin, StorageMigration migration) {
    this(plugin);
    this.migration = migration;
  }

  public void connect() {
    StorageType storageType = configuration.getStorageCredentials().getStorageType();
    plugin
        .loadAndGetDependencyManager()
        .loadStorageDependencies(Collections.singleton(storageType));
    switch (storageType) {
      case MONGODB:
        storageProvider = new MongoDbStorageProvider(configuration);
        break;
      case MYSQL:
        storageProvider = new SqlStorageProvider(plugin, new MySqlConnectionFactory(plugin));
        break;
      case MARIADB:
        storageProvider = new SqlStorageProvider(plugin, new MariaDbConnectionFactory(plugin));
        break;
      case POSTGRESQL:
        storageProvider = new SqlStorageProvider(plugin, new PostgreConnectionFactory(plugin));
        break;
      case H2:
        storageProvider =
            new SqlStorageProvider(
                plugin,
                new H2ConnectionFactory(plugin, plugin.getDataDirectory().resolve("skins-h2")));
        break;
      case SQLITE:
        storageProvider =
            new SqlStorageProvider(
                plugin,
                new SQLiteConnectionFactory(
                    plugin, plugin.getDataDirectory().resolve("skins-sqlite.db")));
        break;
    }

    plugin
        .getScheduler()
        .async()
        .execute(
            () -> {
              storageProvider.connect();
              if (migration != null) {
                migration.migrate(storageProvider);
              }
            });
  }

  public void closeConnection() {
    plugin.getScheduler().async().execute(() -> storageProvider.closeConnection());
  }

  private <T> CompletableFuture<T> makeFuture(Supplier<T> supplier) {
    return CompletableFuture.supplyAsync(supplier, plugin.getScheduler().async());
  }

  private CompletableFuture<Void> makeVoidFuture(Runnable run) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    plugin
        .getScheduler()
        .async()
        .execute(
            () -> {
              run.run();
              future.complete(null);
            });
    return future;
  }

  public CompletableFuture<StoredSkin> findByName(String name) {
    return makeFuture(() -> storageProvider.findByName(name));
  }

  public CompletableFuture<StoredSkin> find(UUID uuid) {
    return makeFuture(() -> storageProvider.find(uuid));
  }

  public CompletableFuture<StoredSkin> acquired(UUID uuid) {
    return makeFuture(() -> storageProvider.acquired(uuid));
  }

  public CompletableFuture<List<StoredSkin>> all() {
    return makeFuture(() -> storageProvider.all());
  }

  public CompletableFuture<Void> storeSkin(StoredSkin storedSkin) {
    return makeVoidFuture(() -> storageProvider.storeSkin(storedSkin));
  }
}
