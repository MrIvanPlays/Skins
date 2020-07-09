package com.mrivanplays.skins.core.storage;

import com.mrivanplays.skins.api.Skin;
import java.util.Objects;

public class StoredSkin {

  private Skin skin;
  private final String ownerName;

  public StoredSkin(Skin skin, String ownerName) {
    this.skin = skin;
    this.ownerName = ownerName;
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
    return getSkin().equals(that.getSkin()) && getOwnerName().equals(that.getOwnerName());
  }

  public boolean equalsNoSkin(StoredSkin o) {
    return getOwnerName().equalsIgnoreCase(o.getOwnerName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSkin(), getOwnerName());
  }

  @Override
  public String toString() {
    return "StoredSkin{" + "skin=" + skin + ", ownerName='" + ownerName + '\'' + '}';
  }
}
