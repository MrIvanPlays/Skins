package com.mrivanplays.skins.api;

import java.util.Optional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** Represents the main object of the skins api. */
public interface SkinsApi {

  /**
   * Gets the player's server set skin. This could also return {@link #getOriginalSkin(Player)} due
   * to how the plugin handles storing skins (to check that you can call {@link
   * #isSetSkinOriginal(Player)})
   *
   * @param player the player you want to get the set skin of
   * @return optional of skin if the player have changed their skin, empty optional otherwise
   */
  Optional<Skin> getSetSkin(@NotNull Player player);

  /**
   * Gets the player's skin, which is set on the premium account of this player. This might be null
   * if the specified player's nickname has no premium account. Also keep in mind that the skin
   * given could not be updated as the already fetched skins get cached and get updated when
   * ./skinset or a join from the specified skin name occurs.
   *
   * @param player the player you want to get the original skin of
   * @return optional of skin if this player is in mojang's database, empty optional otherwise
   */
  default Optional<Skin> getOriginalSkin(@NotNull Player player) {
    return getSkin(player.getName()).getSkin();
  }

  /**
   * Returns whenever the set skin of the specified {@link Player} equals his original skin.
   *
   * @param player the player you want to check's skin
   * @return <code>true</code> if equal, <code>false</code> otherwise
   */
  default boolean isSetSkinOriginal(@NotNull Player player) {
    Optional<Skin> setSkinOpt = getSetSkin(player);
    Optional<Skin> originalSkinOpt = getOriginalSkin(player);
    if (!setSkinOpt.isPresent()) {
      return false;
    }
    if (!originalSkinOpt.isPresent()) {
      return false;
    }
    Skin setSkin = setSkinOpt.get();
    Skin originalSkin = originalSkinOpt.get();
    return setSkin.equals(originalSkin);
  }

  /**
   * Gets the skin of the specified username by fetching {@link java.util.UUID} and skin from the
   * mojang api. The response is never null, however, the parts into the response may be null if the
   * mojang api is down or the server's being rate limited by the mojang api.
   *
   * @param username the username of the premium account you want to get skin
   * @return response with all data, or response with none data if mojang api is down or the
   *     server's being rate limited.
   */
  @NotNull
  MojangResponse getSkin(@NotNull String username);

  /**
   * Sets the skin of the specified player.
   *
   * @param player the player you want to set the skin of
   * @param skin the skin in mojang responce form that you want to set on the player
   * @return whether or not the skin change was successful
   */
  boolean setSkin(@NotNull Player player, @NotNull MojangResponse skin);

  /**
   * Creates a new {@link SkullItemBuilder}
   *
   * @return skull item builder
   * @see SkullItemBuilder
   */
  @NotNull
  SkullItemBuilder newSkullItemBuilder();

  /**
   * Sets a new data provider, for fetching skin and uuid information.
   *
   * @param dataProvider data provider
   */
  void setDataProvider(@NotNull DataProvider dataProvider);
}
