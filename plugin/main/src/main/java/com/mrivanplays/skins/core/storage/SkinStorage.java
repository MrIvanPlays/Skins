package com.mrivanplays.skins.core.storage;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SkinStorage {

  Optional<StoredSkin> getStoredSkin(UUID owner);

  Optional<StoredSkin> getPlayerSetSkin(UUID player);

  void modifyStoredSkin(UUID player, StoredSkin newStoredSkin);

  Set<String> getKeys();
}
