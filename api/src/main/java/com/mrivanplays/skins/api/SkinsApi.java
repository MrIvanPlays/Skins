package com.mrivanplays.skins.api;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/** Represents the main object of the skins api. */
public interface SkinsApi {

  CompletableFuture<Optional<Skin>> getSkin(String name);

  /**
   * Sets a new data provider, for fetching skin and uuid information.
   *
   * @param dataProvider data provider
   */
  void setDataProvider(@NotNull DataProvider dataProvider);

  /**
   * Returns the plugin's information.
   *
   * @return detailed information object about the version of the plugin and the environment the
   *     plugin is being ran on, implementing this api.
   */
  @NotNull
  SkinsInfo getInfo();

  @NotNull
  CompletableFuture<User> getUser(@NotNull String name);

  @NotNull
  CompletableFuture<User> getUser(@NotNull UUID uuid);

  @NotNull
  CompletableFuture<Collection<User>> getUsedBy(@NotNull Skin skin);
}
