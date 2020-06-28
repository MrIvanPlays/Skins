package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.Skin;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StoredSkin {

  private Skin skin;
  private final List<String> acquirers;
  private final String configurationKey;
  private final String name;

  public StoredSkin(Skin skin, String configurationKey, String name) {
    this(skin, new ArrayList<>(), configurationKey, name);
  }

  public StoredSkin(Skin skin, List<String> acquirers, String configurationKey, String name) {
    this.skin = skin;
    this.acquirers = acquirers;
    this.configurationKey = configurationKey;
    this.name = name;
  }

  public String getName() { // owner name
    return name;
  }

  public Skin getSkin() {
    return skin;
  }

  public void setSkin(Skin skin) {
    this.skin = skin;
  }

  public List<String> getAcquirers() {
    return acquirers;
  }

  public void addAcquirer(UUID uuid) {
    if (!acquirers.contains(uuid.toString())) {
      acquirers.add(uuid.toString());
    }
  }

  public void removeAcquirer(UUID uuid) {
    if (acquirers.contains(uuid.toString())) {
      acquirers.remove(uuid.toString());
    }
  }

  public String getConfigurationKey() {
    return configurationKey;
  }

  public StoredSkin duplicate() {
    return new StoredSkin(skin, acquirers, configurationKey, name);
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
    return that.getConfigurationKey().equalsIgnoreCase(this.getConfigurationKey())
        && that.getSkin().getOwner().equals(this.getSkin().getOwner());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getConfigurationKey(), getSkin());
  }
}
