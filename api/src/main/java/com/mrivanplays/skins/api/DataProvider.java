package com.mrivanplays.skins.api;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a data provider, providing needed plugin data. */
public interface DataProvider {

  /**
   * Skins calls this method whenever it needs the name for the specified player uuid. You are able
   * to apply caching strategies on this.
   *
   * @param uuid uuid of the player we need name of
   * @return name, or null if not found
   */
  @Nullable
  String retrieveName(@NotNull UUID uuid);

  /**
   * Skins calls this method whenever it needs the original uuid of the specified player name. You
   * are able to apply caching strategies on this.
   *
   * @param name name of the player we need the uuid of
   * @return uuid, or null if not found
   */
  @Nullable
  UUID retrieveUuid(@NotNull String name);

  /**
   * Skins calls this method whenever it needs to get a skin directly from the api. We recommend to
   * not cache any data as Skins already caches further in the code.
   *
   * @param uuid uuid of the player we want the skin of
   * @return response
   */
  @Nullable
  Skin retrieveSkin(@NotNull UUID uuid);
}
