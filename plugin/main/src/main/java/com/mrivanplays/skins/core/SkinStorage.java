/*
    Copyright (C) 2019 Ivan Pekov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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

    public void modifySkin(StoredSkin newStoredSkin) {
        configuration.set(
                section + "." + newStoredSkin.getConfigurationKey() + ".texture",
                newStoredSkin.getSkin().getTexture()
        );
        configuration.set(
                section + "." + newStoredSkin.getConfigurationKey() + ".signature",
                newStoredSkin.getSkin().getSignature()
        );
        save();
    }

    public void modifyStoredSkin(
            UUID player,
            StoredSkin newStoredSkin
    ) {
        Optional<StoredSkin> currentStoredSkin = getPlayerSetSkin(player);
        if (currentStoredSkin.isPresent()) {
            StoredSkin skin = currentStoredSkin.get();
            StoredSkin duplicate = skin.duplicate();
            duplicate.removeAcquirer(player);
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
            configuration.set(
                    section + "." + duplicate.getConfigurationKey() + ".ownerName",
                    duplicate.getName()
            );
            configuration.set(section + "." + duplicate.getConfigurationKey() + ".acquirers", duplicate.getAcquirers());
            save();
        }
        StoredSkin newDuplicate = newStoredSkin.duplicate();
        newDuplicate.addAcquirer(player);
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
        configuration.set(
                section + "." + newDuplicate.getConfigurationKey() + ".ownerName",
                newDuplicate.getName()
        );
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
            String ownerName = configuration.getString(section + "." + key + ".ownerName");
            String texture = configuration.getString(section + "." + key + ".texture");
            String signature = configuration.getString(section + "." + key + ".signature");
            List<String> acquirers = configuration.getStringList(section + "." + key + ".acquirers");
            StoredSkin storedSkin = new StoredSkin(new Skin(skinOwner, texture, signature), acquirers, key, ownerName);
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
