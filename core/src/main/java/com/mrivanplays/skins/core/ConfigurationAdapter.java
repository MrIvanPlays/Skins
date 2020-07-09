package com.mrivanplays.skins.core;

import java.util.List;

public interface ConfigurationAdapter {

  String getString(String path, String def);

  boolean getBoolean(String path, boolean def);

  int getInt(String path, int def);

  List<String> getStringList(String path, List<String> def);
}
