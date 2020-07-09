package com.mrivanplays.skins.api;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a user object, combining information about the user, which is able to be gathered from
 * all platforms the skins plugin runs on.
 */
public interface User {

  @NotNull
  String getName();

  @NotNull
  UUID getUniqueId();

  /**
   * Returns the original unique id of the user, per the set {@link DataProvider} in {@link
   * SkinsApi}
   *
   * @return original unique id, or null if not found
   */
  @Nullable
  UUID getOriginalUniqueId();

  default Optional<UUID> safeGetOriginalUniqueId() {
    return Optional.ofNullable(getOriginalUniqueId());
  }

  boolean isOnline();

  /**
   * Returns a {@link CompletableFuture} of {@link Optional}, holding {@link Skin}, representing the
   * currently used skin by this user. The returned value can also be {@link #getOriginalSkin()}.
   *
   * @return future, holding the used skin
   */
  CompletableFuture<Optional<Skin>> getSkin();

  /**
   * Returns a {@link CompletableFuture} of {@link Optional}, holding {@link Skin}, representing the
   * original skin of this user. The {@link Optional} may be {@link Optional#empty()} if it cannot
   * be find by the {@link DataProvider} set in {@link SkinsApi}
   *
   * @return future, holding original skin
   */
  CompletableFuture<Optional<Skin>> getOriginalSkin();

  /**
   * Sets this user the {@link Skin} specified.
   *
   * @param skin skin set
   */
  void setSkin(@NotNull Skin skin);
}
