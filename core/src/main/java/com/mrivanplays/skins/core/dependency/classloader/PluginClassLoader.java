package com.mrivanplays.skins.core.dependency.classloader;

import java.nio.file.Path;

/**
 * Credits: lucko/LuckPerms
 * @author lucko
 */
public interface PluginClassLoader {

  void addJarToClassPath(Path file);
}
