package com.mrivanplays.skins.bukkit.abstraction.handle;

import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_12_R1;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_13_R2;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_14_R1;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_15_R1;
import com.mrivanplays.skins.bukkit.abstraction.SupportedVersions;

public class SkinSetterHandler {

  public static SkinSetter getSkinSetter() {
    SupportedVersions version = SupportedVersions.getCurrent();
    if (version == null) {
      return null;
    } else {
      switch (version) {
        case v1_12_R1:
          return new SkinSetter1_12_R1();
        case v1_13_R2:
          return new SkinSetter1_13_R2();
        case v1_14_R1:
          return new SkinSetter1_14_R1();
        case v1_15_R1:
          return new SkinSetter1_15_R1();
        default:
          return null;
      }
    }
  }
}
