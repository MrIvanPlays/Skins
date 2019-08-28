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
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a response object from the mojang api
 */
public final class MojangResponse {

    private final String nickname;
    private final UUID uuid;
    private final Skin skin;

    public MojangResponse(
            @NotNull String nickname,
            @Nullable UUID uuid,
            @Nullable Skin skin
    ) {
        this.nickname = nickname;
        this.uuid = uuid;
        this.skin = skin;
    }

    /**
     * Returns the nickname, for which data was requested.
     *
     * @return nickname (ign)
     */
    @NotNull
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the {@link UUID} of the specified {@link #getNickname()},
     * according to mojang api. The return value may be empty if the
     * plugin wasn't able to fetch it.
     *
     * @return uuid, according to mojang api, or empty optional if failure
     */
    public Optional<UUID> getUuid() {
        return Optional.ofNullable(uuid);
    }

    /**
     * Returns the {@link Skin} of the specified {@link #getUuid()},
     * according to mojang api. The return value may be empty if the
     * plugin wasn't able to fetch {@link #getUuid()} or wasn't able
     * to fetch the skin.
     *
     * @return skin, or empty optional if failure
     */
    public Optional<Skin> getSkin() {
        return Optional.ofNullable(skin);
    }
}
