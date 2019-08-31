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
package com.mrivanplays.skins;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.StoredSkin;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DefaultSkinSetListener implements Listener {

    private final SkinsBukkitPlugin plugin;

    public DefaultSkinSetListener(SkinsBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Optional<StoredSkin> storedSkinOptional = plugin.getSkinStorage().getPlayerSetSkin(player.getUniqueId());
        if (storedSkinOptional.isPresent()) {
            StoredSkin storedSkin = storedSkinOptional.get();
            Skin skin = storedSkin.getSkin();
            plugin.getApi().setSkin(player, checkForSkinUpdate(storedSkin.getName(), skin));
        } else {
            Optional<Skin> skinOptional = plugin.getApi().getOriginalSkin(player);
            if (!skinOptional.isPresent()) {
                return;
            }
            plugin.getApi().setSkin(player, checkForSkinUpdate(player.getName(), skinOptional.get()));
        }
    }

    private Skin checkForSkinUpdate(
            String name,
            Skin skin
    ) {
        MojangResponse response = plugin.getSkinFetcher().apiFetch(name, skin.getOwner()).join();
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
