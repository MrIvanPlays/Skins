package com.mrivanplays.skins.bukkit.abstraction.handle;

import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_12_R1;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_13_R2;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_14_R1;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_15_R1;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_16_R1;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter1_16_R2;
import com.mrivanplays.skins.bukkit.abstraction.SupportedVersions;

public class SkinSetterHandler {

  private static SkinSetter instance = null;

  public static SkinSetter getSkinSetter() {
    if (instance != null) {
      return instance;
    }
    SupportedVersions version = SupportedVersions.getCurrent();
    if (version == null) {
      return null;
    } else {
      if (version == SupportedVersions.v1_12_R1) {
        instance = new SkinSetter1_12_R1();
      } else if (version == SupportedVersions.v1_13_R2) {
        instance = new SkinSetter1_13_R2();
      } else if (version == SupportedVersions.v1_14_R1) {
        instance = new SkinSetter1_14_R1();
      } else if (version == SupportedVersions.v1_15_R1) {
        instance = new SkinSetter1_15_R1();
      } else if (version == SupportedVersions.v1_16_R1) {
        instance = new SkinSetter1_16_R1();
      } else {
        instance = new SkinSetter1_16_R2();
      }
    }
    return instance;
  }
}
