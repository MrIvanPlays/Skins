package com.mrivanplays.skins.bukkit.menuadapters;

import com.mrivanplays.skins.bukkit_general.skull_skinner.ItemSkin;
import com.mrivanplays.skins.core.storage.StoredSkin;
import java.util.UUID;

public class BukkitStoredItemSkin implements ItemSkin {

  private final StoredSkin parent;

  public BukkitStoredItemSkin(StoredSkin parent) {
    this.parent = parent;
  }

  @Override
  public UUID getOwner() {
    return parent.getSkin().getOwner();
  }

  @Override
  public String getTexture() {
    return parent.getSkin().getTexture();
  }

  @Override
  public String getSignature() {
    return parent.getSkin().getSignature();
  }

  public String getOwnerName() {
    return parent.getOwnerName();
  }
}
