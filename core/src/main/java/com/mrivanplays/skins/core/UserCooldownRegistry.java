package com.mrivanplays.skins.core;

public class UserCooldownRegistry {

  public static UserCooldown MENU;
  public static UserCooldown SKIN_SET;

  static {
    MENU = new UserCooldown(90);
    SKIN_SET = new UserCooldown(90);
  }

  public static void ensureInit() {}
}
