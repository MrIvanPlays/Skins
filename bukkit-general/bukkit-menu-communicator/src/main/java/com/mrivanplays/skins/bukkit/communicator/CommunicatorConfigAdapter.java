package com.mrivanplays.skins.bukkit.communicator;

import com.mrivanplays.skins.bukkit_general.SkinsConfigAdapter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CommunicatorConfigAdapter implements SkinsConfigAdapter {

  private final FileConfiguration config;

  public CommunicatorConfigAdapter(File dataFolder) {
    File configFile = new File(dataFolder, "config.yml");
    if (!configFile.exists()) {
      if (!configFile.getParentFile().exists()) {
        configFile.getParentFile().mkdirs();
      }
      try (InputStream in =
          getClass().getClassLoader().getResourceAsStream("bukkit-communicator-config.yml")) {
        Files.copy(in, configFile.getAbsoluteFile().toPath());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    this.config = YamlConfiguration.loadConfiguration(configFile);
  }

  @Override
  public String getSkinMenuPreviousPageLabel() {
    return config.getString("messages.skin-menu-previous-page-label");
  }

  @Override
  public String getSkinMenuNextPageLabel() {
    return config.getString("messages.skin-menu-next-page-label");
  }

  @Override
  public String getSkinMenuClosePageLabel() {
    return config.getString("messages.skin-menu-close-page-label");
  }

  @Override
  public String getSkinMenuHeadName() {
    return config.getString("messages.skin-menu-head-name");
  }

  @Override
  public List<String> getSkinMenuLore() {
    return config.getStringList("messages.skin-menu-lore");
  }

  @Override
  public String getSkinMenuInventory() {
    return config.getString("messages.skin-menu-inventory");
  }

  @Override
  public String getSkinMenuCannotFetchData() {
    return config.getString("messages.skin-menu-cannot-fetch-data");
  }
}
