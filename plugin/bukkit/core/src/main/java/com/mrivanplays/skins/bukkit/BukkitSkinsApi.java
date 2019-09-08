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
package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import java.io.File;
import org.bukkit.entity.Player;

public class BukkitSkinsApi extends AbstractSkinsApi {

  private final SkinsBukkit plugin;

  public BukkitSkinsApi(SkinsBukkit plugin, File dataFolder) {
    super(dataFolder);
    this.plugin = plugin;
  }

  @Override
  protected void setNPCSkin(Player player, Skin skin) {
    plugin.getSkinSetter().setSkin(player, skin);
  }
}
