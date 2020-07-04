package com.mrivanplays.skins.bukkit.core;

import com.mrivanplays.skins.core.AbstractSkinsPlugin;
import com.mrivanplays.skins.core.Scheduler;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public abstract class GeneralSkinsPlugin extends AbstractSkinsPlugin {

  private Scheduler scheduler;

  @Override
  public void enable() {
    scheduler = new Scheduler.Default();
    super.enable();
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
}
