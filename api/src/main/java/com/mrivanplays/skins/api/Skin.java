package com.mrivanplays.skins.api;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/** Represents a skin object */
public final class Skin {

  private final String texture, signature;

  public Skin(@NotNull String texture, @NotNull String signature) {
    this.texture = texture;
    this.signature = signature;
  }

  /**
   * Returns a base64 encoded {@link String}, representing the skin and (if have) the cape of of the
   * skin owner by data from the {@link DataProvider} set in the {@link SkinsApi}
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
    return Objects.equals(getTexture(), skin.getTexture());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTexture(), getSignature());
  }
}
