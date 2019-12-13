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
package com.mrivanplays.skins.bukkit.abstraction.handle;

import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_13_R2;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_14_R1;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_15_R1;
import org.bukkit.Bukkit;

public class SkinSetterHandler {

  public static SkinSetter getSkinSetter() {
    String version = Bukkit.getServer().getClass().getName().replace(".", ",").split(",")[3];
    switch (version) {
      case "v1_13_R2":
        return new SkinSetter1_13_R2();
      case "v1_14_R1":
        return new SkinSetter1_14_R1();
      case "v1_15_R1":
        return new SkinSetter1_15_R1();
      default:
        return null;
    }
  }
}
