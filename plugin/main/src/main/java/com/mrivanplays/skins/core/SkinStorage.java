/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)
 * Copyright 2019 contributors

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.Skin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SkinStorage {

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

    public void modifyStoredSkin(
            Player player,
            StoredSkin newStoredSkin
    ) {
        Optional<StoredSkin> currentStoredSkin = getPlayerSetSkin(player.getUniqueId());
        if (currentStoredSkin.isPresent()) {
            StoredSkin skin = currentStoredSkin.get();
            StoredSkin duplicate = skin.duplicate();
            duplicate.removeAcquirer(player.getUniqueId());
            if (configuration.getString(section + "." + duplicate.getConfigurationKey() + ".texture") == null) {
                configuration.set(
                        section + "." + duplicate.getConfigurationKey() + ".texture",
                        duplicate.getSkin().getTexture()
                );
                configuration.set(
                        section + "." + duplicate.getConfigurationKey() + ".signature",
                        duplicate.getSkin().getSignature()
                );
                configuration.set(
                        section + "." + duplicate.getConfigurationKey() + ".owner",
                        duplicate.getSkin().getOwner().toString()
                );
            }
            configuration.set(section + "." + duplicate.getConfigurationKey() + ".acquirers", duplicate.getAcquirers());
            save();
        }
        StoredSkin newDuplicate = newStoredSkin.duplicate();
        newDuplicate.addAcquirer(player.getUniqueId());
        if (configuration.getString(section + "." + newDuplicate.getConfigurationKey() + ".texture") == null) {
            configuration.set(
                    section + "." + newDuplicate.getConfigurationKey() + ".texture",
                    newDuplicate.getSkin().getTexture()
            );
            configuration.set(
                    section + "." + newDuplicate.getConfigurationKey() + ".signature",
                    newDuplicate.getSkin().getSignature()
            );
            configuration.set(
                    section + "." + newDuplicate.getConfigurationKey() + ".owner",
                    newDuplicate.getSkin().getOwner().toString()
            );
        }
        configuration.set(
                section + "." + newDuplicate.getConfigurationKey() + ".acquirers",
                newDuplicate.getAcquirers()
        );
        save();
    }

    public Set<String> getKeys() {
        if (!configuration.isSet(section)) {
            return Collections.emptySet();
        }
        return configuration.getConfigurationSection(section).getKeys(false);
    }

    private List<StoredSkin> deserialize() {
        List<StoredSkin> storedSkins = new ArrayList<>();
        if (!configuration.isSet(section)) {
            return storedSkins;
        }
        Set<String> keys = getKeys();
        for (String key : keys) {
            UUID skinOwner = UUID.fromString(configuration.getString(section + "." + key + ".owner"));
            String texture = configuration.getString(section + "." + key + ".texture");
            String signature = configuration.getString(section + "." + key + ".signature");
            List<String> acquirers = configuration.getStringList(section + "." + key + ".acquirers");
            StoredSkin storedSkin = new StoredSkin(new Skin(skinOwner, texture, signature), acquirers, key);
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
