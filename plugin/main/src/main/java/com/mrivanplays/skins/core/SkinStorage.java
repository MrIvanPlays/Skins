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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;

public class SkinStorage {

    private final File file;
    private final Gson gson;

    public SkinStorage(File dataFolder) {
        file = new File(dataFolder, "skinstorage.json");
        createFile();
        gson = new Gson();
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
        Set<StoredSkin> storedSkins = new HashSet<>(deserialize());
        if (currentStoredSkin.isPresent()) {
            StoredSkin skin = currentStoredSkin.get();
            StoredSkin duplicate = skin.duplicate();
            storedSkins.remove(skin);
            duplicate.removeAcquirer(player.getUniqueId());
            storedSkins.add(duplicate);
        }
        StoredSkin newDuplicate = newStoredSkin.duplicate();
        storedSkins.remove(newStoredSkin);
        newDuplicate.addAcquirer(player.getUniqueId());
        storedSkins.add(newDuplicate);
        serialize(storedSkins);
    }

    private void serialize(Set<StoredSkin> storedSkin) {
        file.delete();
        createFile();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file))) {
            gson.toJson(storedSkin, writer);
        } catch (IOException ignored) {
        }
    }

    private Set<StoredSkin> deserialize() {
        Set<StoredSkin> list = new HashSet<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file))) {
            JsonArray array = gson.fromJson(reader, JsonArray.class);
            if (array == null || array.size() == 0) {
                return list;
            }
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    return list;
                }
                list.add(gson.fromJson(element.getAsJsonObject(), StoredSkin.class));
            }
        } catch (IOException e) {
            return list;
        }
        return list;
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
}
