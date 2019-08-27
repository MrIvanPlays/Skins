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
package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.StoredSkin;
import java.io.File;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitSkinsApi extends AbstractSkinsApi {

    private final SkinsBukkit plugin;

    public BukkitSkinsApi(
            SkinsBukkit plugin,
            File dataFolder
    ) {
        super(dataFolder);
        this.plugin = plugin;
    }

    @Override
    public boolean setSkin(
            @NotNull Player player,
            @NotNull Skin skin
    ) {
        Optional<StoredSkin> storedSkinOptional = getSkinStorage().getStoredSkin(player);
        if (storedSkinOptional.isPresent()) {
            StoredSkin storedSkin = storedSkinOptional.get();
            storedSkin.removeAcquirer(player.getUniqueId());
            getSkinStorage().modifyStoredSkin(storedSkin);
        } else {
            Optional<StoredSkin> newStoredSkin = getSkinStorage().getStoredSkin(skin.getOwner());
            if (newStoredSkin.isPresent()) {
                StoredSkin sskin = newStoredSkin.get();
                sskin.addAcquirer(player.getUniqueId());
                getSkinStorage().modifyStoredSkin(sskin);
            } else {
                StoredSkin storedSkin = new StoredSkin(skin);
                storedSkin.addAcquirer(player.getUniqueId());
                getSkinStorage().modifyStoredSkin(storedSkin);
            }
        }
        plugin.getSkinSetter().setSkin(player, skin);
        return true;
    }

    @Override
    public boolean isRunningProtocolSupport() {
        return false;
    }
}
