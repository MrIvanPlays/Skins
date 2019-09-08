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

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.handle.SkinSetterHandler;
import com.mrivanplays.skins.core.SkinsPlugin;
import java.io.File;

public class SkinsBukkit implements SkinsPlugin {

  private SkinSetter skinSetter;
  private SkinsApi skinsApi;

  @Override
  public void enable(File dataFolder) {
    skinSetter = SkinSetterHandler.getSkinSetter();
    skinsApi = new BukkitSkinsApi(this, dataFolder);
  }

  @Override
  public SkinsApi getApi() {
    return skinsApi;
  }

  public SkinSetter getSkinSetter() {
    return skinSetter;
  }
}
