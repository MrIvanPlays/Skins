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
