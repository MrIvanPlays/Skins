package com.mrivanplays.skins.bukkit.core;

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.core.AbstractSkinsPlugin;
import com.mrivanplays.skins.core.Scheduler;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.storage.StorageMigration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

public abstract class GeneralSkinsPlugin extends AbstractSkinsPlugin {

  private Scheduler scheduler;
  private final Plugin bukkitPlugin;

  private SkinsConfiguration config;

  public GeneralSkinsPlugin(Plugin bukkitPlugin) {
    this.bukkitPlugin = bukkitPlugin;
  }

  @Override
  public void enable() {
    scheduler =
        new Scheduler.Default(
            (t) -> bukkitPlugin.getServer().getScheduler().runTask(bukkitPlugin, t));
    File file = new File(getDataDirectory().toFile(), "config.yml");
    if (!file.exists()) {
      try (InputStream in = getResourceStream("bukkit-config.yml")) {
        Files.copy(in, file.toPath());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    FileConfiguration konfig = YamlConfiguration.loadConfiguration(file);
    config = new SkinsConfiguration(new BukkitConfigurationAdapter(konfig));
    super.enable();
  }

  @Override
  public void registerApiOnServiceManager(SkinsApi api) {
    bukkitPlugin
        .getServer()
        .getServicesManager()
        .register(SkinsApi.class, api, bukkitPlugin, ServicePriority.High);
  }

  @Override
  public StorageMigration storageMigration() {
    return new BukkitLegacyStorageMigration(bukkitPlugin);
  }

  @Override
  public Scheduler getScheduler() {
    return scheduler;
  }

  @Override
  public String identifyClassLoader(ClassLoader classLoader) {
    try {
      Class<?> pluginClassLoader = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
      if (classLoader.getClass().isAssignableFrom(pluginClassLoader)) {
        Object pluginClassLoaderObject = pluginClassLoader.cast(classLoader);
        Field pluginField = pluginClassLoaderObject.getClass().getDeclaredField("plugin");
        pluginField.setAccessible(true);
        Plugin plugin = (Plugin) pluginField.get(pluginClassLoaderObject);
        return plugin.getName();
      }
    } catch (ClassNotFoundException | NoSuchFieldException ignored) {
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<String> allPlayersForCompletions() {
    List<OfflinePlayer> combined = new ArrayList<>();
    combined.addAll(Arrays.asList(Bukkit.getOfflinePlayers()));
    combined.addAll(Bukkit.getOnlinePlayers());
    return combined.stream().map(OfflinePlayer::getName).collect(Collectors.toList());
  }

  @Override
  public SkinsConfiguration getConfiguration() {
    return config;
  }

  @Override
  public Path getDataDirectory() {
    if (!bukkitPlugin.getDataFolder().exists()) {
      bukkitPlugin.getDataFolder().mkdirs();
    }
    return bukkitPlugin.getDataFolder().toPath().toAbsolutePath();
  }

  @Override
  public InputStream getResourceStream(String name) {
    return bukkitPlugin.getResource(name);
  }
}
