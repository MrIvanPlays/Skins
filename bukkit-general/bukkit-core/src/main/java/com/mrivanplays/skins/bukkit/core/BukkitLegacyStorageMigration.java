package com.mrivanplays.skins.bukkit.core;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.storage.StorageMigration;
import com.mrivanplays.skins.core.storage.StorageProvider;
import com.mrivanplays.skins.core.storage.StoredSkin;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BukkitLegacyStorageMigration implements StorageMigration {

  private final Plugin plugin;

  public BukkitLegacyStorageMigration(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void migrate(StorageProvider provider) {
    File file = new File(plugin.getDataFolder(), "skinstorage.yml");
    if (!file.exists()) {
      return;
    }
    String section = "skins";
    FileConfiguration data = YamlConfiguration.loadConfiguration(file);
    if (!data.isSet(section)) {
      file.delete();
      return;
    }
    Set<String> keys = data.getConfigurationSection(section).getKeys(false);
    for (String key : keys) {
      UUID skinOwner = UUID.fromString(data.getString(section + "." + key + ".owner"));
      String ownerName = data.getString(section + "." + key + ".ownerName");
      String texture = data.getString(section + "." + key + ".texture");
      String signature = data.getString(section + "." + key + ".signature");
      List<String> acquirers = data.getStringList(section + "." + key + ".acquirers");
      StoredSkin storedSkin = new StoredSkin(new Skin(skinOwner, texture, signature), ownerName);
      provider.storeSkin(storedSkin);
      acquirers.stream()
          .map(UUID::fromString)
          .forEach(acquirer -> provider.setAcquirer(acquirer, skinOwner));
    }

    file.delete();
  }
}
