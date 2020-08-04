package com.mrivanplays.skins.core.storage.sql.connection.hikari;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.zaxxer.hikari.HikariConfig;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Credits: lucko/LuckPerms
 *
 * @author lucko
 */
public class MariaDbConnectionFactory extends HikariConnectionFactory {

  public MariaDbConnectionFactory(SkinsPlugin plugin) {
    super(plugin);
  }

  @Override
  protected String getDriverClass() {
    return "org.mariadb.jdbc.MariaDbDataSource";
  }

  @Override
  protected void appendProperties(HikariConfig config, Map<String, String> properties) {
    String propertiesString =
        properties.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining(";"));
    config.addDataSourceProperty("properties", propertiesString);
  }

  @Override
  public Function<String, String> statementTickReplacer() {
    return s -> s.replace("'", "`");
  }
}
