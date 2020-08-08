package com.mrivanplays.skins.api;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents the main object of the skins api. */
public interface SkinsApi {

  /**
   * Returns a {@link CompletableFuture}, holding a {@link Optional} of {@link Skin}, representing
   * the {@link Skin} of the player name specified. If the skin was not found, the returned {@link
   * Optional} in the {@link CompletableFuture} will be a {@link Optional#empty()}
   *
   * @param name the name of the player you want to get the original skin of
   * @return future holding skin optional value
   */
  CompletableFuture<Optional<Skin>> getSkin(@NotNull String name);

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

  /**
   * Returns a {@link User} object, found by the specified name.
   *
   * @param name the name of the user you want to get
   * @return user, or null if cannot be found.
   */
  @Nullable
  User getUser(@NotNull String name);

  default Optional<User> safeGetUser(@NotNull String name) {
    return Optional.ofNullable(getUser(name));
  }

  /**
   * Returns a {@link User} object, found by the specified {@link UUID}
   *
   * @param uuid the uuid of the user you want to get
   * @return user, or null if cannot be found
   */
  @Nullable
  User getUser(@NotNull UUID uuid);

  default Optional<User> safeGetUser(@NotNull UUID uuid) {
    return Optional.ofNullable(getUser(uuid));
  }

  /**
   * Returns a {@link CompletableFuture}, holding {@link Collection} of {@link User}, the users
   * representing the {@link User Users} using the {@link Skin} specified.
   *
   * @param skin the skin you want to get the uses of
   * @return future of user collection
   */
  @NotNull
  CompletableFuture<Collection<User>> getUsedBy(@NotNull Skin skin);
}
