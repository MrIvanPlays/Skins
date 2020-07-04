package com.mrivanplays.skins.core.dependency;

import com.google.common.collect.ImmutableSet;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.dependency.classloader.IsolatedClassLoader;
import com.mrivanplays.skins.core.dependency.relocation.Relocation;
import com.mrivanplays.skins.core.dependency.relocation.RelocationHandler;
import com.mrivanplays.skins.core.storage.StorageType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

/**
 * Original code: lucko/LuckPerms
 *
 * @author lucko
 */
public class DependencyManager {

  /** The plugin instance */
  private final SkinsPlugin plugin;
  /** A registry containing plugin specific behaviour for dependencies. */
  private final DependencyRegistry registry;
  /** The path where library jars are cached. */
  private final Path cacheDirectory;

  /** A map of dependencies which have already been loaded. */
  private final EnumMap<Dependency, Path> loaded = new EnumMap<>(Dependency.class);
  /** A map of isolated classloaders which have been created. */
  private final Map<ImmutableSet<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
  /** Cached relocation handler instance. */
  private @MonotonicNonNull RelocationHandler relocationHandler = null;

  public DependencyManager(SkinsPlugin plugin) {
    this.plugin = plugin;
    this.registry = new DependencyRegistry();
    this.cacheDirectory = setupCacheDirectory(plugin);
  }

  private synchronized RelocationHandler getRelocationHandler() {
    if (this.relocationHandler == null) {
      this.relocationHandler = new RelocationHandler(this);
    }
    return this.relocationHandler;
  }

  public IsolatedClassLoader obtainClassLoaderWith(Set<Dependency> dependencies) {
    ImmutableSet<Dependency> set = ImmutableSet.copyOf(dependencies);

    for (Dependency dependency : dependencies) {
      if (!this.loaded.containsKey(dependency)) {
        throw new IllegalStateException("Dependency " + dependency + " is not loaded.");
      }
    }

    synchronized (this.loaders) {
      IsolatedClassLoader classLoader = this.loaders.get(set);
      if (classLoader != null) {
        return classLoader;
      }

      URL[] urls =
          set.stream()
              .map(this.loaded::get)
              .map(
                  file -> {
                    try {
                      return file.toUri().toURL();
                    } catch (MalformedURLException e) {
                      throw new RuntimeException(e);
                    }
                  })
              .toArray(URL[]::new);

      classLoader = new IsolatedClassLoader(urls);
      this.loaders.put(set, classLoader);
      return classLoader;
    }
  }

  public void loadStorageDependencies(Set<StorageType> storageTypes) {
    loadDependencies(this.registry.resolveStorageDependencies(storageTypes));
  }

  public void loadDependencies(Set<Dependency> dependencies) {
    CountDownLatch latch = new CountDownLatch(dependencies.size());

    for (Dependency dependency : dependencies) {
      this.plugin
          .getScheduler()
          .async()
          .execute(
              () -> {
                try {
                  loadDependency(dependency);
                } catch (Throwable e) {
                  this.plugin
                      .getLogger()
                      .severe("Unable to load dependency " + dependency.name() + ".");
                  e.printStackTrace();
                } finally {
                  latch.countDown();
                }
              });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void loadDependency(Dependency dependency) throws Exception {
    if (this.loaded.containsKey(dependency)) {
      return;
    }

    Path file = remapDependency(dependency, downloadDependency(dependency));

    this.loaded.put(dependency, file);

    if (this.registry.shouldAutoLoad(dependency)) {
      this.plugin.getPluginClassLoader().addJarToClassPath(file);
    }
  }

  private Path downloadDependency(Dependency dependency) throws DependencyDownloadException {
    Path file = this.cacheDirectory.resolve(dependency.getFileName() + ".jar");

    // if the file already exists, don't attempt to re-download it.
    if (Files.exists(file)) {
      return file;
    }

    DependencyDownloadException lastError = null;

    // attempt to download the dependency from each repo in order.
    for (DependencyRepository repo : DependencyRepository.values()) {
      try {
        repo.download(dependency, file);
        return file;
      } catch (DependencyDownloadException e) {
        lastError = e;
      }
    }

    throw Objects.requireNonNull(lastError);
  }

  private Path remapDependency(Dependency dependency, Path normalFile) throws Exception {
    List<Relocation> rules = new ArrayList<>(dependency.getRelocations());

    if (rules.isEmpty()) {
      return normalFile;
    }

    Path remappedFile = this.cacheDirectory.resolve(dependency.getFileName() + "-remapped.jar");

    // if the remapped source exists already, just use that.
    if (Files.exists(remappedFile)) {
      return remappedFile;
    }

    getRelocationHandler().remap(normalFile, remappedFile, rules);
    return remappedFile;
  }

  private static Path setupCacheDirectory(SkinsPlugin plugin) {
    Path cacheDirectory = plugin.getDataDirectory().resolve("libs");
    try {
      if (!Files.exists(cacheDirectory)) {
        Files.createDirectories(cacheDirectory);
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to create libs directory", e);
    }

    return cacheDirectory;
  }
}
