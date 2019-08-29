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

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSkinsApi implements SkinsApi {

    private final SkinFetcher skinFetcher;
    private final SkinStorage skinStorage;

    public AbstractSkinsApi(File dataFolder) {
        skinStorage = new SkinStorage(dataFolder);
        skinFetcher = new SkinFetcher(skinStorage);
    }

    @Override
    public Optional<Skin> getSetSkin(@NotNull Player player) {
        return skinStorage.getPlayerSetSkin(player.getUniqueId()).map(StoredSkin::getSkin);
    }

    @Override
    @NotNull
    public MojangResponse getSkin(@NotNull String username) {
        return skinFetcher.getSkin(username);
    }

    @Override
    public void setSkin(
            @NotNull Player player,
            @NotNull Skin skin
    ) {
        setSkin(player, skin, skinFetcher.fetchName(skin.getOwner()).join());
    }

    public void setSkin(
            Player player,
            Skin skin,
            String name
    ) {
        Optional<StoredSkin> newStoredSkin = skinStorage.getStoredSkin(skin.getOwner());
        if (newStoredSkin.isPresent()) {
            StoredSkin skinStored = newStoredSkin.get();
            skinStorage.modifyStoredSkin(player.getUniqueId(), skinStored);
        } else {
            Set<String> keys = skinStorage.getKeys();
            List<Integer> keysAsInts = keys.stream().map(Integer::parseInt).collect(Collectors.toList());
            keysAsInts.sort(new Comparator<Integer>() {
                @Override
                public int compare(
                        Integer o1,
                        Integer o2
                ) {
                    return Integer.compare(o1, o2);
                }
            }.reversed());
            int biggestNumber;
            if (keysAsInts.isEmpty()) {
                biggestNumber = 0;
            } else {
                biggestNumber = keysAsInts.get(0);
            }
            keysAsInts.clear();
            keys.clear();
            StoredSkin skinStored = new StoredSkin(
                    skin,
                    Integer.toString(biggestNumber + 1),
                    name
            );
            skinStorage.modifyStoredSkin(player.getUniqueId(), skinStored);
        }
        setNPCSkin(player, skin);
    }

    protected void setNPCSkin(
            Player player,
            Skin skin
    ) {
    }

    public SkinFetcher getSkinFetcher() {
        return skinFetcher;
    }

    public SkinStorage getSkinStorage() {
        return skinStorage;
    }
}
