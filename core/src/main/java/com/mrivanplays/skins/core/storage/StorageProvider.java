package com.mrivanplays.skins.core.storage;

import java.util.UUID;

public interface StorageProvider {

  void connect();

  void storeSkin(StoredSkin skin);

  StoredSkin findByName(String name);

  StoredSkin find(UUID uuid);

  StoredSkin acquired(UUID uuid);

  void closeConnection();
}
