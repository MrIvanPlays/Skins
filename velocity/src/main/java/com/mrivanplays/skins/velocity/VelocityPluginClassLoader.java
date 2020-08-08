package com.mrivanplays.skins.velocity;

import com.mrivanplays.skins.core.dependency.classloader.PluginClassLoader;
import java.nio.file.Path;

public class VelocityPluginClassLoader implements PluginClassLoader {

  private final VelocityPlugin plugin;

  public VelocityPluginClassLoader(VelocityPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void addJarToClassPath(Path file) {
    plugin.getProxy().getPluginManager().addToClasspath(plugin, file);
  }
}
