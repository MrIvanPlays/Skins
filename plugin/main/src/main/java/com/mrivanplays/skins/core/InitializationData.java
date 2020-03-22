package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import com.mrivanplays.skins.core.storage.SkinStorage;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

public class InitializationData {

  private final SkinStorage skinStorage;
  private final Function<SkullItemBuilderData, ItemStack> transformer;
  private DataProvider dataProvider;

  public InitializationData(
      SkinStorage skinStorage,
      Function<SkullItemBuilderData, ItemStack> transformer,
      DataProvider dataProvider) {
    this.skinStorage = skinStorage;
    this.transformer = transformer;
    this.dataProvider = dataProvider;
  }

  public SkinStorage getSkinStorage() {
    return skinStorage;
  }

  public Function<SkullItemBuilderData, ItemStack> getTransformer() {
    return transformer;
  }

  public DataProvider getDataProvider() {
    return dataProvider;
  }
}
