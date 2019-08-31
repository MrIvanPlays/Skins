/*
    Copyright (C) 2019 Ivan Pekov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.mrivanplays.skins.api;

import java.util.Optional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the main object of the skins api.
 */
public interface SkinsApi {

    /**
     * Gets the player's server set skin. This could also return
     * {@link #getOriginalSkin(Player)} due to how the plugin
     * handles storing skins.
     *
     * @param player the player you want to get the set skin of
     * @return optional of skin if the player have changed their skin, empty optional otherwise
     */
    Optional<Skin> getSetSkin(@NotNull Player player);

    /**
     * Gets the player's skin, which is set on the premium account of this
     * player. This might be null if the specified player's nickname has no
     * premium account.
     *
     * @param player the player you want to get the original skin of
     * @return optional of skin if this player is in mojang's database, empty optional otherwise
     */
    default Optional<Skin> getOriginalSkin(@NotNull Player player) {
        MojangResponse response = getSkin(player.getName());
        return Optional.ofNullable(response.getSkin().isPresent() ? response.getSkin().get() : null);
    }

    /**
     * Gets the skin of the specified username by fetching {@link java.util.UUID}
     * and skin from the mojang api. The response is never null, however, the
     * parts into the response may be null if the mojang api is down or the
     * server's being rate limited by the mojang api.
     *
     * @param username the username of the premium account you want to get skin
     * @return response with all data, or response with none data if mojang api is down or the server's being rate limited.
     */
    @NotNull
    MojangResponse getSkin(@NotNull String username);

    /**
     * Sets the skin of the specified player.
     *
     * @param player the player you want to set the skin of
     * @param skin the skin you want to set on the player
     */
    void setSkin(
            @NotNull Player player,
            @NotNull Skin skin
    );
}
