package com.mrivanplays.skins.core.storage.sql.connection.file;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.dependency.Dependency;
import com.mrivanplays.skins.core.dependency.classloader.IsolatedClassLoader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Properties;
import java.util.function.Function;

/**
 * Credits: lucko/LuckPerms
 * @author lucko
 */
public class H2ConnectionFactory extends FlatfileConnectionFactory {

  private final Driver driver;
  private NonClosableConnection connection;

  public H2ConnectionFactory(SkinsPlugin plugin, Path file) {
    super(file);

    Path data = file.getParent().resolve("skins.db.mv.db");
    if (Files.exists(data)) {
      try {
        Files.move(data, getWriteFile());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    IsolatedClassLoader classLoader =
        plugin
            .getDependencyManager()
            .obtainClassLoaderWith(EnumSet.of(Dependency.H2_DRIVER));
    try {
      Class<?> driverClass = classLoader.loadClass("org.h2.Driver");
      Method loadMethod = driverClass.getMethod("load");
      this.driver = (Driver) loadMethod.invoke(null);
    } catch (InvocationTargetException
        | NoSuchMethodException
        | IllegalAccessException
        | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      Connection connection = driver.connect("jdbc:h2:" + file.toString(), new Properties());
      if (connection != null) {
        this.connection = NonClosableConnection.wrap(connection);
      }
    }

    if (connection == null) {
      throw new SQLException("Unable to get connection.");
    }

    return connection;
  }

  @Override
  public Function<String, String> statementTickReplacer() {
    return s -> s.replace("'", "`");
  }

  @Override
  public void close() throws SQLException {
    if (connection != null) {
      connection.shutdown();
    }
  }

  @Override
  protected Path getWriteFile() {
    return super.file.getParent().resolve(super.file.getFileName().toString() + ".mv.db");
  }
}
