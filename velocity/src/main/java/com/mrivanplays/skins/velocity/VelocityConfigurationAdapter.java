package com.mrivanplays.skins.velocity;

import com.google.common.reflect.TypeToken;
import com.mrivanplays.skins.core.ConfigurationAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class VelocityConfigurationAdapter implements ConfigurationAdapter {

  private ConfigurationNode config;

  public VelocityConfigurationAdapter(
      VelocityPlugin plugin, Function<String, InputStream> getResourceStream) {
    try {
      Path configPath = Paths.get(plugin.getDataDirectory() + "/config.yml");
      if (!Files.exists(configPath)) {
        Files.createDirectories(plugin.getDataDirectory());
        try (InputStream in = getResourceStream.apply("velocity-config.yml")) {
          Files.copy(in, configPath);
        }
      }

      ConfigurationLoader<ConfigurationNode> configurationLoader =
          YAMLConfigurationLoader.builder().setPath(configPath).build();
      config = configurationLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getString(String path, String def) {
    return config.getNode(path).getString(def);
  }

  @Override
  public boolean getBoolean(String path, boolean def) {
    return config.getNode(path).getBoolean(def);
  }

  @Override
  public int getInt(String path, int def) {
    return config.getNode(path).getInt(def);
  }

  @Override
  public List<String> getStringList(String path, List<String> def) {
    try {
      return config.getNode(path).getList(new TypeToken<String>() {}, def);
    } catch (ObjectMappingException e) {
      return Collections.emptyList();
    }
  }
}
