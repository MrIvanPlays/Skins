package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.SkinsApi;
import java.io.File;
import java.util.logging.Logger;

public interface SkinsPlugin {

  void enable(File dataFolder, Logger logger);

  SkinsApi getApi();
}
