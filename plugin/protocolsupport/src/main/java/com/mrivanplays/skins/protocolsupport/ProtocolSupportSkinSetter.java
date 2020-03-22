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
import com.mrivanplays.skins.core.MojangResponseHolder;
import com.mrivanplays.skins.core.StoredSkin;
import java.util.Optional;
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
    Optional<StoredSkin> storedSkinOptional =
        skinsApi.getSkinStorage().getPlayerSetSkin(profile.getUUID());
    if (storedSkinOptional.isPresent()) {
      StoredSkin storedSkin = storedSkinOptional.get();
      Skin skin = storedSkin.getSkin();
      Skin setSkin = checkForSkinUpdate(storedSkin.getName(), skin);
      if (!skin.equals(setSkin)) {
        StoredSkin newStoredSkin = storedSkin.duplicate();
        newStoredSkin.setSkin(setSkin);
        skinsApi.getSkinStorage().modifySkin(newStoredSkin);
      }
      event.addProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
    } else {
      MojangResponseHolder responseHolder =
          skinsApi.getSkinFetcher().getSkin(profile.getName(), profile.getUUID());
      if (responseHolder.isJustFetched()) {
        Optional<Skin> skinOptional = responseHolder.getResponse().getSkin();
        if (!skinOptional.isPresent()) {
          return;
        }
        Skin skin = skinOptional.get();
        skinsApi.modifyStoredSkin(profile.getUUID(), skin, profile.getName());
        event.addProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
      } else {
        MojangResponse response = responseHolder.getResponse();
        Optional<Skin> skinOptional = response.getSkin();
        if (!skinOptional.isPresent()) {
          return;
        }
        Skin skin = skinOptional.get();
        Skin setSkin = checkForSkinUpdate(response.getNickname(), skin);
        if (!skin.equals(setSkin)) {
          skinsApi.modifyStoredSkin(profile.getUUID(), setSkin, profile.getName());
        }
        event.addProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
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
