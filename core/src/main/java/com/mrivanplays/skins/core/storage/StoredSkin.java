package com.mrivanplays.skins.core.storage;

import com.mrivanplays.skins.api.Skin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StoredSkin {

  private final Skin skin;
  private final String ownerName;
  private final UUID ownerUUID;

  private Set<UUID> acquirers;

  public StoredSkin(Skin skin, String ownerName, UUID ownerUUID) {
    this.skin = skin;
    this.ownerName = ownerName;
    this.ownerUUID = ownerUUID;
    this.acquirers = new HashSet<>();
  }

  public void addAcquirer(UUID acquirer) {
    acquirers.add(acquirer);
  }

  public void removeAcquirer(UUID acquirer) {
    acquirers.remove(acquirer);
  }

  public Set<UUID> getAcquirers() {
    return acquirers;
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
