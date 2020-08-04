package com.mrivanplays.skins.core.dependency.classloader;

import com.google.common.base.Suppliers;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Credits: lucko/LuckPerms
 *
 * @author lucko
 */
public class ReflectionClassLoader implements PluginClassLoader {
  private final URLClassLoader classLoader;

  private final Supplier<Method> addUrlMethod;

  public ReflectionClassLoader(Object bootstrap, Logger logger) throws IllegalStateException {
    ClassLoader classLoader = bootstrap.getClass().getClassLoader();
    if (classLoader instanceof URLClassLoader) {
      this.classLoader = (URLClassLoader) classLoader;
    } else {
      throw new IllegalStateException("ClassLoader is not instance of URLClassLoader");
    }

    if (isJava9OrNewer()) {
      logger.info(
          "It is safe to ignore any warning printed following this message "
              + "starting with 'WARNING: An illegal reflective access operation has occurred, Illegal reflective "
              + "access by "
              + getClass().getName()
              + "'. This is intended, and will not have any impact on the "
              + "operation of Skins.");
    }

    this.addUrlMethod =
        Suppliers.memoize(
            () -> {
              try {
                Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
                return addUrlMethod;
              } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
              }
            });
  }

  @Override
  public void addJarToClassPath(Path file) {
    try {
      this.addUrlMethod.get().invoke(this.classLoader, file.toUri().toURL());
    } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("JavaReflectionMemberAccess")
  private static boolean isJava9OrNewer() {
    try {
      // method was added in the Java 9 release
      Runtime.class.getMethod("version");
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }
}
