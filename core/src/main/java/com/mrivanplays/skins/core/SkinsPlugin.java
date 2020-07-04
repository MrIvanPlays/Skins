package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.Environment;
import com.mrivanplays.skins.core.command.Command;
import java.util.UUID;
import java.util.logging.Logger;

public interface SkinsPlugin {

  SkinsUser obtainUser(String name);

  SkinsUser obtainUser(UUID uuid);

  Environment getEnvironment();

  void registerCommand(String name, Command command);

  Logger getLogger();


}
