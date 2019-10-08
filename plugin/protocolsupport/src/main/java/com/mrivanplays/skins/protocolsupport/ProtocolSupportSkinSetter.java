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
package com.mrivanplays.skins.protocolsupport;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsApi;
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

  private final AbstractSkinsApi skinsApi;

  public ProtocolSupportSkinSetter(AbstractSkinsApi skinsApi) {
    this.skinsApi = skinsApi;
  }

  @EventHandler
  public void on(PlayerProfileCompleteEvent event) {
    Profile profile = event.getConnection().getProfile();
    Optional<StoredSkin> storedSkinOptional = skinsApi.getSkinStorage()
        .getPlayerSetSkin(profile.getUUID());
    if (storedSkinOptional.isPresent()) {
      StoredSkin storedSkin = storedSkinOptional.get();
      Skin skin = storedSkin.getSkin();
      Skin setSkin = checkForSkinUpdate(storedSkin.getName(), skin);
      if (skin.equals(setSkin)) {
        event.addProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
        return;
      }
      StoredSkin newStoredSkin = storedSkin.duplicate();
      newStoredSkin.setSkin(setSkin);
      skinsApi.getSkinStorage().modifyStoredSkin(profile.getUUID(), newStoredSkin);
      event.addProperty(
          new ProfileProperty("textures", setSkin.getTexture(), setSkin.getSignature()));
    } else {
      MojangResponse response = skinsApi.getSkinFetcher().getSkin(profile.getName());
      if (response.getSkin().isPresent()) {
        Skin skin = response.getSkin().get();
        Skin setSkin = checkForSkinUpdate(profile.getName(), skin);
        Optional<StoredSkin> playerOriginal = skinsApi.getSkinStorage()
            .getStoredSkin(setSkin.getOwner());
        if (playerOriginal.isPresent()) {
          StoredSkin storedSkin = playerOriginal.get();
          if (storedSkin.getSkin().equals(setSkin)) {
            event.addProperty(
                new ProfileProperty("textures", setSkin.getTexture(), setSkin.getSignature()));
            return;
          }
          StoredSkin duplicate = storedSkin.duplicate();
          duplicate.setSkin(setSkin);
          skinsApi.getSkinStorage().modifySkin(storedSkin);
        } else {
          Set<String> keys = skinsApi.getSkinStorage().getKeys();
          List<Integer> keysAsInts =
              keys.stream().map(Integer::parseInt).collect(Collectors.toList());
          keysAsInts.sort(
              new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
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
          StoredSkin storedSkin =
              new StoredSkin(setSkin, Integer.toString(biggestNumber + 1), profile.getName());
          storedSkin.addAcquirer(profile.getUUID());
          skinsApi.getSkinStorage().modifyStoredSkin(profile.getUUID(), storedSkin);
        }
        event.addProperty(
            new ProfileProperty("textures", setSkin.getTexture(), setSkin.getSignature()));
      }
    }
  }

  private Skin checkForSkinUpdate(String name, Skin skin) {
    MojangResponse response = skinsApi.getSkinFetcher().apiFetch(name, skin.getOwner()).join();
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
