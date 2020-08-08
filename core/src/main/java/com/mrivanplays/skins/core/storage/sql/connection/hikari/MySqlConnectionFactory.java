package com.mrivanplays.skins.core.storage.sql.connection.hikari;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.zaxxer.hikari.HikariConfig;
import java.util.Map;
import java.util.function.Function;

/**
 * Credits: lucko/LuckPerms
 *
 * @author lucko
 */
public class MySqlConnectionFactory extends HikariConnectionFactory {

  public MySqlConnectionFactory(SkinsPlugin plugin) {
    super(plugin);
  }

  @Override
  protected String getDriverClass() {
    return "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
  }

  @Override
  protected void appendProperties(HikariConfig config, Map<String, String> properties) {
    // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
    properties.putIfAbsent("cachePrepStmts", "true");
    properties.putIfAbsent("prepStmtCacheSize", "250");
    properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
    properties.putIfAbsent("useServerPrepStmts", "true");
    properties.putIfAbsent("useLocalSessionState", "true");
    properties.putIfAbsent("rewriteBatchedStatements", "true");
    properties.putIfAbsent("cacheResultSetMetadata", "true");
    properties.putIfAbsent("cacheServerConfiguration", "true");
    properties.putIfAbsent("elideSetAutoCommits", "true");
    properties.putIfAbsent("maintainTimeStats", "false");
    properties.putIfAbsent("alwaysSendSetIsolation", "false");
    properties.putIfAbsent("cacheCallableStmts", "true");

    // append configurable properties
    super.appendProperties(config, properties);
  }

  @Override
  public Function<String, String> statementTickReplacer() {
    return s -> s.replace("'", "`");
  }
}
