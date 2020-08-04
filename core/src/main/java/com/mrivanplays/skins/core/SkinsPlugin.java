package com.mrivanplays.skins.core;

import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsInfo;
import com.mrivanplays.skins.core.command.CommandSource;
import com.mrivanplays.skins.core.dependency.DependencyManager;
import com.mrivanplays.skins.core.dependency.classloader.PluginClassLoader;
import com.mrivanplays.skins.core.storage.Storage;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface SkinsPlugin {

  SkinsUser obtainUser(String name);

  SkinsUser obtainUser(UUID uuid);

  SkinsInfo getInfo();

  void registerCommand(
      String name, Predicate<CommandSource> permissionCheck, Command<CommandSource> command);

  Logger getLogger();

  PluginClassLoader getPluginClassLoader();

  Path getDataDirectory();

  Scheduler getScheduler();

  SkinsConfiguration getConfiguration();

  Storage getStorage();

  DependencyManager getDependencyManager();

  String identifyClassLoader(ClassLoader classLoader);

  InputStream getResourceStream(String name);

  List<String> allPlayersForCompletions();

  void dispatchSkinSet(AbstractSkinsUser user, Optional<Skin> skinOpt, String name);
}
