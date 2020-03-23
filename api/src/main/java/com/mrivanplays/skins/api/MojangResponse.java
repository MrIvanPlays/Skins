package com.mrivanplays.skins.api;

import java.util.Objects;
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
   * Returns the {@link UUID} of the specified {@link #getNickname()}, according to the {@link
   * DataProvider} set in the {@link SkinsApi}. The return value may be empty if the plugin wasn't
   * able to fetch it.
   *
   * @return uuid, according to mojang api, or empty optional if failure
   */
  public Optional<UUID> getUuid() {
    return Optional.ofNullable(uuid);
  }

  /**
   * Returns the {@link Skin} of the specified {@link #getUuid()}, according to the {@link
   * DataProvider} set in the {@link SkinsApi}. The return value may be empty if the plugin wasn't
   * able to fetch {@link #getUuid()} or wasn't able to fetch the skin.
   *
   * @return skin, or empty optional if failure
   */
  public Optional<Skin> getSkin() {
    return Optional.ofNullable(skin);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MojangResponse that = (MojangResponse) o;
    if (that.getSkin().isPresent() && getSkin().isPresent()) {
      return that.getSkin().get().equals(getSkin().get())
          && Objects.equals(getNickname(), that.getNickname());
    }
    return Objects.equals(getNickname(), that.getNickname());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getNickname(), getUuid(), getSkin());
  }
}
