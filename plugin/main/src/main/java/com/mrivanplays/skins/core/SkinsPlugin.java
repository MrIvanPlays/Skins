package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.io.File;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;

public interface SkinsPlugin {

  void enable(InitializationData initializationData);

  SkinsApi getApi();
}
