package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.io.File;
import java.util.concurrent.Executor;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

public class InitializationData {

  private final File dataFolder;
  private final Function<SkullItemBuilderData, ItemStack> transformer;
  private DataProvider dataProvider;
  private final Executor async;

  public InitializationData(
      File dataFolder,
      Function<SkullItemBuilderData, ItemStack> transformer,
      DataProvider dataProvider,
      Executor async) {
    this.dataFolder = dataFolder;
    this.transformer = transformer;
    this.dataProvider = dataProvider;
    this.async = async;
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

  public Executor getAsync() {
    return async;
  }
}
