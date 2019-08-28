/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)
 * Copyright 2019 contributors

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.mrivanplays.skins.api;

import java.util.Optional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the main object of the skins api.
 */
public interface SkinsApi {

    /**
     * Gets the player's server set skin. When ProtocolSupport is
     * being installed on the server, this will always be empty
     * optional.
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
     * Sets the skin of the specified player. <p>
     * NOTE: This method will do nothing if the server's running
     * ProtocolSupport.
     *
     * @param player the player you want to set the skin of
     * @param skin the skin you want to set on the player
     * @return success state
     */
    boolean setSkin(
            @NotNull Player player,
            @NotNull Skin skin
    );

    /**
     * Returns whenever this server is running ProtocolSupport
     *
     * @return <code>true</code> if running, otherwise <code>false</code>
     */
    boolean isRunningProtocolSupport();
}
