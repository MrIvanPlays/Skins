package com.mrivanplays.skins.core.storage;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.config.SkinsConfiguration;
import java.util.Collections;
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

  private CompletableFuture<StoredSkin> makeSkinFuture(Supplier<StoredSkin> supplier) {
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
    return makeSkinFuture(() -> storageProvider.findByName(name));
  }

  public CompletableFuture<StoredSkin> find(UUID uuid) {
    return makeSkinFuture(() -> storageProvider.find(uuid));
  }

  public CompletableFuture<StoredSkin> acquired(UUID uuid) {
    return makeSkinFuture(() -> storageProvider.acquired(uuid));
  }

  public CompletableFuture<Void> storeSkin(StoredSkin storedSkin) {
    return makeVoidFuture(() -> storageProvider.storeSkin(storedSkin));
  }
}
