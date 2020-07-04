package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.Environment;
import com.mrivanplays.skins.core.command.Command;
import com.mrivanplays.skins.core.dependency.classloader.PluginClassLoader;
import com.mrivanplays.skins.core.scheduler.Scheduler;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

public interface SkinsPlugin {

  SkinsUser obtainUser(String name);

  SkinsUser obtainUser(UUID uuid);

  Environment getEnvironment();

  void registerCommand(String name, Command command);

  Logger getLogger();

  PluginClassLoader getPluginClassLoader();

  Path getDataDirectory();

  Scheduler getScheduler();
}
