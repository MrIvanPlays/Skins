package com.mrivanplays.skins.bukkit.core;

import com.mrivanplays.skins.core.ConfigurationAdapter;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public class BukkitConfigurationAdapter implements ConfigurationAdapter {

  private final FileConfiguration config;

  public BukkitConfigurationAdapter(FileConfiguration config) {
    this.config = config;
  }

  @Override
  public String getString(String path, String def) {
    return config.getString(path, def);
  }

  @Override
  public boolean getBoolean(String path, boolean def) {
    return config.getBoolean(path, def);
  }

  @Override
  public int getInt(String path, int def) {
    return config.getInt(path, def);
  }

  @Override
  public List<String> getStringList(String path, List<String> def) {
    List<String> took = config.getStringList(path);
    if (took.isEmpty()) {
      return def;
    }
    return took;
  }
}
