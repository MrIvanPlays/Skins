package com.mrivanplays.skins.api;

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
   * Returns the owner of this skin by data from the Mojang API
   *
   * @return owner
   */
  @NotNull
  public UUID getOwner() {
    return owner;
  }

  /**
   * Returns a base64 encoded {@link String}, representing the skin and (if have) the cape of of the
   * {@link #getOwner()} by data from the Mojang API
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
    if (!(o instanceof Skin)) {
      return false;
    }
    Skin other = (Skin) o;
    // signature not included as this may differ
    // because the mojang api is returning different signature
    // after 2-3 requests
    return other.getOwner().equals(owner) && other.getTexture().equalsIgnoreCase(texture);
  }
}
