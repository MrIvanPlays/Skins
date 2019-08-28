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
