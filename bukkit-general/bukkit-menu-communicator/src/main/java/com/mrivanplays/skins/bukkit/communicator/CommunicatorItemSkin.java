package com.mrivanplays.skins.bukkit.communicator;

import com.mrivanplays.skins.bukkit_general.skull_skinner.ItemSkin;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class CommunicatorItemSkin implements ItemSkin {

  private final UUID owner;
  private final String texture, signature, ownerName;

  public CommunicatorItemSkin(UUID owner, String texture, String signature, String ownerName) {
    this.owner = owner;
    this.texture = texture;
    this.signature = signature;
    this.ownerName = ownerName;
  }

  @Override
  public UUID getOwner() {
    return owner;
  }

  @Override
  public String getTexture() {
    return texture;
  }

  @Override
  public String getSignature() {
    return signature;
  }

  @Nullable
  public String getOwnerName() {
    return ownerName;
  }
}
