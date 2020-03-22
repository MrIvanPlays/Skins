package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.Skin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class SkinStorage {

  private final File file;
  private final FileConfiguration configuration;
  private final String section;

  public SkinStorage(File dataFolder) {
    file = new File(dataFolder, "skinstorage.yml");
    createFile();
    configuration = YamlConfiguration.loadConfiguration(file);
    section = "skins";
  }

  public Optional<StoredSkin> getStoredSkin(UUID owner) {
    return deserialize().stream()
        .filter(skin -> skin.getSkin().getOwner().equals(owner))
        .findFirst();
  }

  public Optional<StoredSkin> getPlayerSetSkin(UUID player) {
    return deserialize().stream()
        .filter(skin -> skin.getAcquirers().contains(player.toString()))
        .findFirst();
  }

  public void modifySkin(StoredSkin newStoredSkin) {
    configuration.set(
        section + "." + newStoredSkin.getConfigurationKey() + ".texture",
        newStoredSkin.getSkin().getTexture());
    configuration.set(
        section + "." + newStoredSkin.getConfigurationKey() + ".signature",
        newStoredSkin.getSkin().getSignature());
    save();
  }

  public void modifyStoredSkin(UUID player, StoredSkin newStoredSkin) {
    UUID owner = newStoredSkin.getSkin().getOwner();
    Optional<StoredSkin> playerSet = getPlayerSetSkin(player);
    if (playerSet.isPresent()) {
      StoredSkin skin = playerSet.get();
      if (skin.getSkin().getOwner().equals(owner)) {
        if (!skin.getSkin().getTexture().equalsIgnoreCase(newStoredSkin.getSkin().getTexture())) {
          modifySkin(newStoredSkin);
        }
      } else {
        // current
        StoredSkin duplicate = skin.duplicate();
        duplicate.removeAcquirer(player);
        configuration.set(
            section + "." + duplicate.getConfigurationKey() + ".acquirers",
            duplicate.getAcquirers());

        // new
        if (!getKeys().contains(newStoredSkin.getConfigurationKey())) {
          StoredSkin newDuplicate = newStoredSkin.duplicate();
          newDuplicate.addAcquirer(player);
          configuration.set(
              section + "." + newDuplicate.getConfigurationKey() + ".texture",
              newDuplicate.getSkin().getTexture());
          configuration.set(
              section + "." + newDuplicate.getConfigurationKey() + ".signature",
              newDuplicate.getSkin().getSignature());
          configuration.set(
              section + "." + newDuplicate.getConfigurationKey() + ".owner",
              newDuplicate.getSkin().getOwner().toString());
          configuration.set(
              section + "." + newDuplicate.getConfigurationKey() + ".ownerName",
              newDuplicate.getName());
          configuration.set(
              section + "." + newDuplicate.getConfigurationKey() + ".acquirers",
              newDuplicate.getAcquirers());
        } else {
          StoredSkin nowStored = getStoredSkin(newStoredSkin.getSkin().getOwner()).get();
          if (!nowStored
              .getSkin()
              .getTexture()
              .equalsIgnoreCase(newStoredSkin.getSkin().getTexture())) {
            configuration.set(
                section + "." + newStoredSkin.getConfigurationKey() + ".texture",
                newStoredSkin.getSkin().getTexture());
            configuration.set(
                section + "." + newStoredSkin.getConfigurationKey() + ".signature",
                newStoredSkin.getSkin().getSignature());
          }
          nowStored.addAcquirer(player);
          configuration.set(
              section + "." + nowStored.getConfigurationKey() + ".acquirers",
              nowStored.getAcquirers());
        }

        save();
      }
    } else {
      if (!getKeys().contains(newStoredSkin.getConfigurationKey())) {
        StoredSkin newDuplicate = newStoredSkin.duplicate();
        newDuplicate.addAcquirer(player);
        configuration.set(
            section + "." + newDuplicate.getConfigurationKey() + ".texture",
            newDuplicate.getSkin().getTexture());
        configuration.set(
            section + "." + newDuplicate.getConfigurationKey() + ".signature",
            newDuplicate.getSkin().getSignature());
        configuration.set(
            section + "." + newDuplicate.getConfigurationKey() + ".owner",
            newDuplicate.getSkin().getOwner().toString());
        configuration.set(
            section + "." + newDuplicate.getConfigurationKey() + ".ownerName",
            newDuplicate.getName());
        configuration.set(
            section + "." + newDuplicate.getConfigurationKey() + ".acquirers",
            newDuplicate.getAcquirers());
      } else {
        StoredSkin nowStored = getStoredSkin(newStoredSkin.getSkin().getOwner()).get();
        if (!nowStored
            .getSkin()
            .getTexture()
            .equalsIgnoreCase(newStoredSkin.getSkin().getTexture())) {
          configuration.set(
              section + "." + newStoredSkin.getConfigurationKey() + ".texture",
              newStoredSkin.getSkin().getTexture());
          configuration.set(
              section + "." + newStoredSkin.getConfigurationKey() + ".signature",
              newStoredSkin.getSkin().getSignature());
        }
        nowStored.addAcquirer(player);
        configuration.set(
            section + "." + nowStored.getConfigurationKey() + ".acquirers",
            nowStored.getAcquirers());
      }

      save();
    }
  }

  public Set<String> getKeys() {
    if (!configuration.isSet(section)) {
      return Collections.emptySet();
    }
    return configuration.getConfigurationSection(section).getKeys(false);
  }

  public List<StoredSkin> deserialize() {
    List<StoredSkin> storedSkins = new ArrayList<>();
    if (!configuration.isSet(section)) {
      return storedSkins;
    }
    Set<String> keys = getKeys();
    for (String key : keys) {
      UUID skinOwner = UUID.fromString(configuration.getString(section + "." + key + ".owner"));
      String ownerName = configuration.getString(section + "." + key + ".ownerName");
      String texture = configuration.getString(section + "." + key + ".texture");
      String signature = configuration.getString(section + "." + key + ".signature");
      List<String> acquirers = configuration.getStringList(section + "." + key + ".acquirers");
      StoredSkin storedSkin =
          new StoredSkin(new Skin(skinOwner, texture, signature), acquirers, key, ownerName);
      storedSkins.add(storedSkin);
    }
    return storedSkins;
  }

  private void createFile() {
    if (!file.exists()) {
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      try {
        file.createNewFile();
      } catch (IOException ignored) {
      }
    }
  }

  private void save() {
    try {
      configuration.save(file);
    } catch (IOException ignored) {
    }
  }
}
