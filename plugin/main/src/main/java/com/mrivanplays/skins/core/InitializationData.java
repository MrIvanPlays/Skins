package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.SkinsVersionInfo;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.io.File;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

public class InitializationData {

  private final File dataFolder;
  private final Function<SkullItemBuilderData, ItemStack> transformer;
  private DataProvider dataProvider;
  private SkinsVersionInfo versionInfo;

  public InitializationData(
      File dataFolder,
      Function<SkullItemBuilderData, ItemStack> transformer,
      DataProvider dataProvider,
      SkinsVersionInfo versionInfo) {
    this.dataFolder = dataFolder;
    this.transformer = transformer;
    this.dataProvider = dataProvider;
    this.versionInfo = versionInfo;
  }

  public File getDataFolder() {
    return dataFolder;
  }

  public Function<SkullItemBuilderData, ItemStack> getTransformer() {
    return transformer;
  }

  public DataProvider getDataProvider() {
    return dataProvider;
  }

  public SkinsVersionInfo getVersionInfo() {
    return versionInfo;
  }
}
