package com.mrivanplays.skins.core.storage.sql.connection.file;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.dependency.Dependency;
import com.mrivanplays.skins.core.dependency.classloader.IsolatedClassLoader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Properties;
import java.util.function.Function;

public class SQLiteConnectionFactory extends FlatfileConnectionFactory {

  private final Method createConnectionMethod;

  private NonClosableConnection connection;

  public SQLiteConnectionFactory(SkinsPlugin plugin, Path file) {
    super(file);

    IsolatedClassLoader classLoader =
        plugin
            .loadAndGetDependencyManager()
            .obtainClassLoaderWith(EnumSet.of(Dependency.SQLITE_DRIVER));
    try {
      Class<?> jdbcClass = classLoader.loadClass("org.sqlite.JDBC");
      this.createConnectionMethod =
          jdbcClass.getMethod("createConnection", String.class, Properties.class);
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private Connection createConnection(String url) throws SQLException {
    try {
      return (Connection) this.createConnectionMethod.invoke(null, url, new Properties());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof SQLException) {
        throw ((SQLException) e.getCause());
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      Connection connection = createConnection("jdbc:sqlite:" + file.toString());
      if (connection != null) {
        this.connection = NonClosableConnection.wrap(connection);
      }
    }

    if (connection == null) {
      throw new SQLException("Unable to get a connection.");
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
}
