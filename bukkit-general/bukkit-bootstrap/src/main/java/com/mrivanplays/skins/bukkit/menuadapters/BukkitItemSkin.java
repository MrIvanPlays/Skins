package com.mrivanplays.skins.bukkit.menuadapters;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.bukkit_general.skull_skinner.ItemSkin;
import java.util.UUID;

public class BukkitItemSkin implements ItemSkin {

  private final Skin parent;

  public BukkitItemSkin(Skin parent) {
    this.parent = parent;
  }

  @Override
  public UUID getOwner() {
    return parent.getOwner();
  }

  @Override
  public String getTexture() {
    return parent.getTexture();
  }

  @Override
  public String getSignature() {
    return parent.getSignature();
  }
}
