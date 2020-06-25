package com.mrivanplays.skins.api;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a response object from the mojang api */
public final class MojangResponse {

  private final String nickname;
  private final Skin skin;

  public MojangResponse(@NotNull String nickname, @Nullable Skin skin) {
    this.nickname = nickname;
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
   * Returns the {@link Skin}, held by this response.
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
    return Objects.hash(getNickname(), getSkin());
  }
}
