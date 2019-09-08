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
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a response object from the mojang api */
public final class MojangResponse {

  private final String nickname;
  private final UUID uuid;
  private final Skin skin;

  public MojangResponse(@NotNull String nickname, @Nullable UUID uuid, @Nullable Skin skin) {
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
   * Returns the {@link UUID} of the specified {@link #getNickname()}, according to mojang api. The
   * return value may be empty if the plugin wasn't able to fetch it.
   *
   * @return uuid, according to mojang api, or empty optional if failure
   */
  public Optional<UUID> getUuid() {
    return Optional.ofNullable(uuid);
  }

  /**
   * Returns the {@link Skin} of the specified {@link #getUuid()}, according to mojang api. The
   * return value may be empty if the plugin wasn't able to fetch {@link #getUuid()} or wasn't able
   * to fetch the skin.
   *
   * @return skin, or empty optional if failure
   */
  public Optional<Skin> getSkin() {
    return Optional.ofNullable(skin);
  }
}
