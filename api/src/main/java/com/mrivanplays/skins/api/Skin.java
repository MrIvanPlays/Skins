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

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a skin object
 */
public final class Skin {

    private final UUID owner;
    private final String texture;
    private final String signature;

    public Skin(
            @NotNull UUID owner,
            @NotNull String texture,
            @NotNull String signature
    ) {
        this.owner = owner;
        this.texture = texture;
        this.signature = signature;
    }

    /**
     * Returns the owner of this skin by data from the Mojang API
     *
     * @return owner
     */
    @NotNull
    public UUID getOwner() {
        return owner;
    }

    /**
     * Returns a base64 encoded {@link String}, representing the skin
     * and (if have) the cape of of the {@link #getOwner()} by data
     * from the Mojang API
     *
     * @return texture
     */
    @NotNull
    public String getTexture() {
        return texture;
    }

    /**
     * Returns a base64 encoded {@link String} which represents
     * signed data (signature, made by) using Yggdrasil's private key
     *
     * @return signature
     */
    @NotNull
    public String getSignature() {
        return signature;
    }
}
