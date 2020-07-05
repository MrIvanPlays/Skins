package com.mrivanplays.skins.core.storage;

import com.mrivanplays.skins.api.Skin;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class StoredSkin {

  private Skin skin;
  private final String ownerName;

  private Set<UUID> acquirers;

  public StoredSkin(Skin skin, String ownerName) {
    this.skin = skin;
    this.ownerName = ownerName;
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

  public void setSkin(Skin skin) {
    this.skin = skin;
  }

  public String getOwnerName() {
    return ownerName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StoredSkin that = (StoredSkin) o;
    return getSkin().equals(that.getSkin()) &&
        getOwnerName().equals(that.getOwnerName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSkin(), getOwnerName());
  }

  @Override
  public String toString() {
    return "StoredSkin{" +
        "skin=" + skin +
        ", ownerName='" + ownerName + '\'' +
        ", acquirers=" + acquirers +
        '}';
  }
}
