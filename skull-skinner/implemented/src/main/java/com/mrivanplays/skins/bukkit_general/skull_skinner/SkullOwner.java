package com.mrivanplays.skins.bukkit_general.skull_skinner;

import java.util.UUID;

public final class SkullOwner {

  private final String ownerName;
  private final UUID ownerUUID;

  public SkullOwner(String ownerName, UUID ownerUUID) {
    this.ownerName = ownerName;
    this.ownerUUID = ownerUUID;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public UUID getOwnerUUID() {
    return ownerUUID;
  }
}
