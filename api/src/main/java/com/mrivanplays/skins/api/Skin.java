package com.mrivanplays.skins.api;

import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** Represents a skin object */
public final class Skin {

  private final UUID owner;
  private final String texture;
  private final String signature;

  public Skin(@NotNull UUID owner, @NotNull String texture, @NotNull String signature) {
    this.owner = owner;
    this.texture = texture;
    this.signature = signature;
  }

  /**
   * Returns the owner of this skin by data from the {@link DataProvider} set in the {@link
   * SkinsApi}
   *
   * @return owner
   */
  @NotNull
  public UUID getOwner() {
    return owner;
  }

  /**
   * Returns a base64 encoded {@link String}, representing the skin and (if have) the cape of of the
   * {@link #getOwner()} by data from the {@link DataProvider} set in the {@link SkinsApi}
   *
   * @return texture
   */
  @NotNull
  public String getTexture() {
    return texture;
  }

  /**
   * Returns a base64 encoded {@link String} which represents signed data (signature, made by) using
   * Yggdrasil's private key
   *
   * @return signature
   */
  @NotNull
  public String getSignature() {
    return signature;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    // signature not included as it may differ
    // the default provider used after 2-3 requests it gives different signature.
    Skin skin = (Skin) o;
    return Objects.equals(getOwner(), skin.getOwner())
        && Objects.equals(getTexture(), skin.getTexture());
  }

  @Override
  public int hashCode() {
    // signature not included as it may differ
    return Objects.hash(getOwner(), getTexture());
  }
}
