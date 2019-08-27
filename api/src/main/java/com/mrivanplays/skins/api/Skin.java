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
