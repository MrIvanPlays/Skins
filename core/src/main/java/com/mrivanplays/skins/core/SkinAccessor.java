package com.mrivanplays.skins.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.storage.Storage;
import com.mrivanplays.skins.core.storage.StoredSkin;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class SkinAccessor {

  private final Cache<UUID, Skin> SKIN_CACHE =
      Caffeine.newBuilder().expireAfterWrite(90, TimeUnit.SECONDS).maximumSize(10000).build();

  private DataProvider dataProvider;
  private final Scheduler scheduler;
  private final Storage storage;

  public SkinAccessor(DataProvider dataProvider, Scheduler scheduler, Storage storage) {
    this.dataProvider = dataProvider;
    this.scheduler = scheduler;
    this.storage = storage;
  }

  public DataProvider getDataProvider() {
    return dataProvider;
  }

  public void setDataProvider(DataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  public CompletableFuture<Skin> getSkin(String name) {
    Preconditions.checkNotNull(name, "name");
    CompletableFuture<Skin> future = new CompletableFuture<>();
    scheduler
        .async()
        .execute(
            () -> {
              // look up in storage before fetching
              StoredSkin storedSkin = storage.findByName(name).join();
              UUID uuid;
              if (storedSkin != null) {
                uuid = storedSkin.getSkin().getOwner();
              } else {
                uuid = dataProvider.retrieveUuid(name);
                if (uuid == null) {
                  future.complete(null);
                  return;
                }
              }

              Skin skin = SKIN_CACHE.getIfPresent(uuid);
              if (skin != null) {
                future.complete(skin);
                return;
              }

              Skin fetched = dataProvider.retrieveSkin(uuid);
              if (fetched == null) {
                if (storedSkin != null) {
                  fetched = storedSkin.getSkin();
                }
              }
              if (fetched != null) {
                SKIN_CACHE.put(uuid, fetched);
              }
              future.complete(fetched);
            });
    return future;
  }

  public CompletableFuture<Skin> getSkin(UUID uuid, boolean checkStorage) {
    Preconditions.checkNotNull(uuid, "uuid");
    return CompletableFuture.supplyAsync(
        () -> {
          Skin skin = SKIN_CACHE.getIfPresent(uuid);
          if (skin != null) {
            return skin;
          }

          Skin fetched = dataProvider.retrieveSkin(uuid);
          if (checkStorage) {
            if (fetched == null) {
              StoredSkin storedSkin = storage.find(uuid).join();
              if (storedSkin != null) {
                fetched = storedSkin.getSkin();
              }
            }
          }
          if (fetched != null) {
            SKIN_CACHE.put(uuid, fetched);
          }
          return fetched;
        },
        scheduler.async());
  }
}
