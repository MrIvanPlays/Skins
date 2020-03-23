package com.mrivanplays.skins.api;

import com.google.common.base.Preconditions;
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
   * @deprecated this method will always take from cache without updating the skin, use {@link
   *     #getSetSkinResponse(Player)}
   */
  @Deprecated
  Optional<Skin> getSetSkin(@NotNull Player player);

  /**
   * Gets the player's server set skin. This could also return {@link #getOriginalSkin(Player)} due
   * to how the plugin handles storing skins. The skin will be always updated if can.
   *
   * @param player the player you want to get the set skin of
   * @return response
   */
  @NotNull
  MojangResponse getSetSkinResponse(@NotNull Player player);

  /**
   * Gets the player's skin, which is set on the premium account of this player. This might be null
   * if the specified player's nickname has no premium account.
   *
   * @param player the player you want to get the original skin of
   * @return optional of skin if this player is in the data provider's database, empty optional
   *     otherwise
   * @deprecated use {@link #getOriginalSkinResponse(Player)}
   */
  @Deprecated
  default Optional<Skin> getOriginalSkin(@NotNull Player player) {
    return getSkin(player.getName()).getSkin();
  }

  /**
   * Gets the player's skin, which is set on the premium account of this player.
   *
   * @param player the player you want to get the original skin of
   * @return response
   * @see #getSkin(String)
   */
  @NotNull
  default MojangResponse getOriginalSkinResponse(@NotNull Player player) {
    Preconditions.checkNotNull(player, "player");
    return getSkin(player.getName());
  }

  /**
   * Returns whenever the set skin of the specified {@link Player} equals his original skin.
   *
   * @param player the player you want to check's skin
   * @return <code>true</code> if equal, <code>false</code> otherwise
   */
  default boolean isSetSkinOriginal(@NotNull Player player) {
    MojangResponse setSkin = getSetSkinResponse(player);
    MojangResponse originalSkin = getOriginalSkinResponse(player);
    return setSkin.equals(originalSkin);
  }

  /**
   * Gets the skin of the specified username by fetching {@link java.util.UUID} and skin from the
   * {@link DataProvider} set. The response is never null, however, the parts into the response may
   * be null if the data provider wasn't able to fetch them, for one reason or another. It is
   * recommended to run this asynchronously.
   *
   * @param username the username of the premium account you want to get skin
   * @return response with all data, or response with none data if data provider wasn't able to
   *     fetch it
   */
  @NotNull
  MojangResponse getSkin(@NotNull String username);

  /**
   * Sets the skin of the specified player.
   *
   * @param player the player you want to set the skin of
   * @param skin the skin in response form that you want to set on the player
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
