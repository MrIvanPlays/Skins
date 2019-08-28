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
package com.mrivanplays.skins.protocolsupport;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.SkinFetcher;
import com.mrivanplays.skins.core.SkinStorage;
import com.mrivanplays.skins.core.StoredSkin;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import protocolsupport.api.events.PlayerProfileCompleteEvent;
import protocolsupport.api.utils.Profile;
import protocolsupport.api.utils.ProfileProperty;

public class ProtocolSupportSkinSetter implements Listener {

    private final SkinStorage skinStorage;
    private final SkinFetcher skinFetcher;

    public ProtocolSupportSkinSetter(
            SkinStorage skinStorage,
            SkinFetcher skinFetcher
    ) {
        this.skinStorage = skinStorage;
        this.skinFetcher = skinFetcher;
    }

    @EventHandler
    public void on(PlayerProfileCompleteEvent event) {
        Profile profile = event.getConnection().getProfile();
        Optional<StoredSkin> storedSkinOptional = skinStorage.getPlayerSetSkin(profile.getUUID());
        if (storedSkinOptional.isPresent()) {
            StoredSkin storedSkin = storedSkinOptional.get();
            Skin skin = storedSkin.getSkin();
            Skin setSkin = checkForSkinUpdate(storedSkin.getName(), skin);
            StoredSkin newStoredSkin = storedSkin.duplicate();
            newStoredSkin.setSkin(setSkin);
            skinStorage.modifyStoredSkin(profile.getUUID(), newStoredSkin);
            event.addProperty(new ProfileProperty("textures", setSkin.getTexture(), setSkin.getSignature()));
        } else {
            MojangResponse response = skinFetcher.getSkin(profile.getName());
            if (response.getSkin().isPresent()) {
                Skin skin = response.getSkin().get();
                Skin setSkin = checkForSkinUpdate(profile.getName(), skin);
                Optional<StoredSkin> playerOriginal = skinStorage.getStoredSkin(setSkin.getOwner());
                if (playerOriginal.isPresent()) {
                    StoredSkin storedSkin = playerOriginal.get();
                    StoredSkin duplicate = storedSkin.duplicate();
                    duplicate.setSkin(setSkin);
                    skinStorage.modifySkin(storedSkin);
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
                    StoredSkin storedSkin = new StoredSkin(
                            setSkin,
                            Integer.toString(biggestNumber + 1),
                            profile.getName()
                    );
                    storedSkin.addAcquirer(profile.getUUID());
                    skinStorage.modifyStoredSkin(profile.getUUID(), storedSkin);
                }
                event.addProperty(new ProfileProperty("textures", setSkin.getTexture(), setSkin.getSignature()));
            }
        }
    }

    private Skin checkForSkinUpdate(
            String name,
            Skin skin
    ) {
        MojangResponse response = skinFetcher.apiFetch(name, skin.getOwner()).join();
        if (response.getSkin().isPresent()) {
            Skin fetched = response.getSkin().get();
            if (skin.getTexture().equalsIgnoreCase(fetched.getTexture())) {
                return skin;
            } else {
                return fetched;
            }
        } else {
            return skin;
        }
    }
}
