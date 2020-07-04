package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.User;
import com.mrivanplays.skins.core.command.CommandSource;

public interface SkinsUser extends User, CommandSource {

  // kinda hacky tbh
  // but java's being stupid soooo yh
  User toUser();

  void openSkinMenu();
}
