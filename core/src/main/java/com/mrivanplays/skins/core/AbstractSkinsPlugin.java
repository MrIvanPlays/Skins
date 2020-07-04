package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.SkinsApiProvider;
import com.mrivanplays.skins.core.command.CommandSkinInfo;
import com.mrivanplays.skins.core.command.CommandSkinMenu;
import com.mrivanplays.skins.core.command.CommandSkinReload;
import com.mrivanplays.skins.core.command.CommandSkinSet;
import com.mrivanplays.skins.core.dependency.Dependency;
import com.mrivanplays.skins.core.dependency.DependencyManager;
import com.mrivanplays.skins.core.storage.Storage;
import com.mrivanplays.skins.core.storage.StorageMigration;
import java.util.EnumSet;

public abstract class AbstractSkinsPlugin implements SkinsPlugin {

  private DependencyManager dependencyManager;
  private SkinsConfiguration configuration;
  private Storage storage;
  private SkinsApiImpl apiImpl;

  public void enable() {
    dependencyManager = new DependencyManager(this);
    dependencyManager.loadDependencies(EnumSet.of(Dependency.CAFFEINE));
    configuration = new SkinsConfiguration();
    generateConfig(configuration);
    storage = new Storage(this, storageMigration());
    storage.connect();
    apiImpl = new SkinsApiImpl(this);
    SkinsApiProvider.set(apiImpl);
    registerCommand("skinset", new CommandSkinSet(this));
    registerCommand("skinmenu", new CommandSkinMenu(this));
    registerCommand("skininfo", new CommandSkinInfo(this));
    registerCommand("skinreload", new CommandSkinReload());
  }

  public void disable() {
    storage.closeConnection();
  }

  public abstract void generateConfig(SkinsConfiguration config);

  public StorageMigration storageMigration() {
    return null;
  }

  public SkinsApiImpl getApiImpl() {
    return apiImpl;
  }

  @Override
  public SkinsConfiguration getConfiguration() {
    return configuration;
  }

  @Override
  public Storage getStorage() {
    return storage;
  }

  @Override
  public DependencyManager getDependencyManager() {
    return dependencyManager;
  }
}
