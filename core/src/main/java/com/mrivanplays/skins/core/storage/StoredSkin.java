package com.mrivanplays.skins.core.storage;

import com.mrivanplays.skins.api.Skin;
import java.util.UUID;

public class StoredSkin {

  private final Skin skin;
  private final String ownerName;
  private final UUID ownerUUID;

  public StoredSkin(Skin skin, String ownerName, UUID ownerUUID) {
    this.skin = skin;
    this.ownerName = ownerName;
    this.ownerUUID = ownerUUID;
  }

  public Skin getSkin() {
    return skin;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public UUID getOwnerUUID() {
    return ownerUUID;
  }
}
