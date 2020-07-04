package com.mrivanplays.skins.core.storage.sql;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.storage.StorageProvider;
import com.mrivanplays.skins.core.storage.StoredSkin;
import com.mrivanplays.skins.core.storage.sql.connection.SQLConnectionFactory;
import java.io.IOException;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SqlStorageProvider implements StorageProvider {

  private final SkinsPlugin plugin;
  private final SQLConnectionFactory connectionFactory;

  public SqlStorageProvider(SkinsPlugin plugin, SQLConnectionFactory connectionFactory) {
    this.plugin = plugin;
    this.connectionFactory = connectionFactory;
  }

  @Override
  public void connect() {
    try {
      connectionFactory.connect();
      boolean tableExists;
      try (Connection connection = connectionFactory.getConnection()) {
        tableExists = tableExists(connection, "skins_storage");
      }

      if (!tableExists) {
        applySchema();
      }
    } catch (IOException | SQLException e) {
      e.printStackTrace();
    }
  }

  private void applySchema() throws IOException, SQLException {
    List<String> statements;

    String storageName =
        plugin
            .loadAndGetConfiguration()
            .getStorageCredentials()
            .getStorageType()
            .name()
            .toLowerCase();
    String schemaFileName = "com/mrivanplays/skins/schema/" + storageName + ".sql";

    try (InputStream is = plugin.getResourceStream(schemaFileName)) {
      if (is == null) {
        throw new IOException("Couldn't locate schema file for " + storageName);
      }

      statements =
          SchemaReader.getStatements(is).stream()
              .map(connectionFactory.statementTickReplacer())
              .collect(Collectors.toList());
    }

    try (Connection connection = connectionFactory.getConnection()) {
      boolean utf8mb4Unsupported = false;

      try (Statement s = connection.createStatement()) {
        for (String query : statements) {
          s.addBatch(query);
        }

        try {
          s.executeBatch();
        } catch (BatchUpdateException e) {
          if (e.getMessage().contains("Unknown character set")) {
            utf8mb4Unsupported = true;
          } else {
            throw e;
          }
        }
      }

      if (utf8mb4Unsupported) {
        try (Statement s = connection.createStatement()) {
          for (String query : statements) {
            s.addBatch(query.replace("utf8mb4", "utf8"));
          }
          s.executeBatch();
        }
      }
    }
  }

  private boolean tableExists(Connection connection, String table) throws SQLException {
    try (ResultSet rs = connection.getMetaData().getTables(null, null, "%", null)) {
      while (rs.next()) {
        if (rs.getString(3).equalsIgnoreCase(table)) {
          return true;
        }
      }
      return false;
    }
  }

  @Override
  public void storeSkin(StoredSkin skin) {
    try (Connection connection = connectionFactory.getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              connectionFactory
                  .statementTickReplacer()
                  .apply("SELECT * FROM 'skins_storage' WHERE ownerUUID = ?"))) {
        statement.setString(1, skin.getOwnerUUID().toString());
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            try (PreparedStatement update =
                connection.prepareStatement(
                    connectionFactory
                        .statementTickReplacer()
                        .apply(
                            "UPDATE 'skins_storage' SET ownerName = ?, texture = ?, signature = ?, acquirers = ? WHERE id = ?"))) {
              update.setString(1, skin.getOwnerName());
              update.setString(2, skin.getSkin().getTexture());
              update.setString(3, skin.getSkin().getSignature());
              String acquirers =
                  skin.getAcquirers().stream().map(UUID::toString).collect(Collectors.joining(","));
              update.setString(4, acquirers);
              update.setInt(5, result.getInt("id"));
            }
          } else {
            try (PreparedStatement insert =
                connection.prepareStatement(
                    connectionFactory
                        .statementTickReplacer()
                        .apply(
                            "INSERT INTO 'skins_storage' (ownerName, ownerUUID, texture, signature, acquirers) VALUES (?, ?, ?, ?, ?)"))) {
              insert.setString(1, skin.getOwnerName());
              insert.setString(2, skin.getOwnerUUID().toString());
              insert.setString(3, skin.getSkin().getTexture());
              insert.setString(4, skin.getSkin().getSignature());

              String acquirers =
                  skin.getAcquirers().stream().map(UUID::toString).collect(Collectors.joining(","));
              insert.setString(5, acquirers);
              insert.executeUpdate();
            }
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public StoredSkin findByName(String name) {
    try (Connection connection = connectionFactory.getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              connectionFactory
                  .statementTickReplacer()
                  .apply("SELECT * FROM 'skins_storage' WHERE ownerName = ?"))) {
        statement.setString(1, name);
        StoredSkin storedSkin = getStoredSkin(statement);
        if (storedSkin != null) {
          return storedSkin;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public StoredSkin find(UUID uuid) {
    try (Connection connection = connectionFactory.getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              connectionFactory
                  .statementTickReplacer()
                  .apply("SELECT * FROM 'skins_storage' WHERE ownerUUID = ?"))) {
        statement.setString(1, uuid.toString());
        StoredSkin storedSkin = getStoredSkin(statement);
        if (storedSkin != null) {
          return storedSkin;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  private StoredSkin getStoredSkin(PreparedStatement statement) throws SQLException {
    try (ResultSet result = statement.executeQuery()) {
      if (result.next()) {
        return getFromResult(result);
      }
    }
    return null;
  }

  private StoredSkin getFromResult(ResultSet result) throws SQLException {
    StoredSkin storedSkin =
        new StoredSkin(
            new Skin(result.getString("texture"), result.getString("signature")),
            result.getString("ownerName"),
            UUID.fromString(result.getString("ownerUUID")));
    List<UUID> acquirers =
        Arrays.stream(result.getString("acquirers").split(","))
            .map(UUID::fromString)
            .collect(Collectors.toList());
    storedSkin.getAcquirers().addAll(acquirers);
    return storedSkin;
  }

  @Override
  public StoredSkin acquired(UUID uuid) {
    try (Connection connection = connectionFactory.getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              connectionFactory.statementTickReplacer().apply("SELECT * FROM 'skins_storage'"))) {
        try (ResultSet result = statement.executeQuery()) {
          while (result.next()) {
            Set<UUID> acquirers =
                Arrays.stream(result.getString("acquirers").split(","))
                    .map(UUID::fromString)
                    .collect(Collectors.toSet());
            if (acquirers.contains(uuid)) {
              return getFromResult(result);
            }
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void closeConnection() {
    try {
      connectionFactory.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
