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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        plugin.getConfiguration().getStorageCredentials().getStorageType().name().toLowerCase();
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
        statement.setString(1, skin.getSkin().getOwner().toString());
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            try (PreparedStatement update =
                connection.prepareStatement(
                    connectionFactory
                        .statementTickReplacer()
                        .apply(
                            "UPDATE 'skins_storage' SET 'ownerName' = ?, 'texture' = ?, 'signature' = ? WHERE id = ?"))) {
              update.setString(1, skin.getOwnerName());
              update.setString(2, skin.getSkin().getTexture());
              update.setString(3, skin.getSkin().getSignature());
              update.setInt(4, result.getInt("id"));
              update.executeUpdate();
            }
          } else {
            try (PreparedStatement insert =
                connection.prepareStatement(
                    connectionFactory
                        .statementTickReplacer()
                        .apply(
                            "INSERT INTO 'skins_storage' ('ownerName', 'ownerUUID', 'texture', 'signature') VALUES (?, ?, ?, ?)"))) {
              insert.setString(1, skin.getOwnerName());
              insert.setString(2, skin.getSkin().getOwner().toString());
              insert.setString(3, skin.getSkin().getTexture());
              insert.setString(4, skin.getSkin().getSignature());

              insert.executeUpdate();
            }
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setAcquirer(UUID uuid, UUID skinAcquired) {
    try (Connection connection = connectionFactory.getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              connectionFactory
                  .statementTickReplacer()
                  .apply("SELECT * FROM 'skins_acquired' WHERE 'acquirer_uuid' = ?"))) {
        statement.setString(1, uuid.toString());
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            // update
            try (PreparedStatement update =
                connection.prepareStatement(
                    connectionFactory
                        .statementTickReplacer()
                        .apply(
                            "UPDATE 'skins_acquired' SET 'acquired_uuid' = ? WHERE 'acquirer_uuid' = ?"))) {
              update.setString(2, uuid.toString());
              update.setString(1, skinAcquired.toString());
              update.executeUpdate();
            }
          } else {
            // insert
            try (PreparedStatement insert =
                connection.prepareStatement(
                    connectionFactory
                        .statementTickReplacer()
                        .apply(
                            "INSERT INTO 'skins_acquired' ('acquirer_uuid', 'acquired_uuid') VALUES (?, ?)"))) {
              insert.setString(1, uuid.toString());
              insert.setString(2, skinAcquired.toString());
              insert.executeUpdate();
            }
          }
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
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
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return new StoredSkin(
                new Skin(
                    UUID.fromString(result.getString("ownerUUID")),
                    result.getString("texture"),
                    result.getString("signature")),
                result.getString("ownerName"));
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  @Override
  public StoredSkin find(UUID uuid) {
    try (Connection connection = connectionFactory.getConnection()) {
      return find(uuid, connection);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private StoredSkin find(UUID uuid, Connection connection) throws SQLException {
    try (PreparedStatement statement =
        connection.prepareStatement(
            connectionFactory
                .statementTickReplacer()
                .apply("SELECT * FROM 'skins_storage' WHERE 'ownerUUID' = ?"))) {
      statement.setString(1, uuid.toString());
      try (ResultSet result = statement.executeQuery()) {
        if (result.next()) {
          return new StoredSkin(
              new Skin(
                  UUID.fromString(result.getString("ownerUUID")),
                  result.getString("texture"),
                  result.getString("signature")),
              result.getString("ownerName"));
        } else {
          return null;
        }
      }
    }
  }

  @Override
  public StoredSkin acquired(UUID uuid) {
    try (Connection connection = connectionFactory.getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              connectionFactory
                  .statementTickReplacer()
                  .apply("SELECT * FROM 'skins_acquired' WHERE 'acquirer_uuid' = ?"))) {
        statement.setString(1, uuid.toString());
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return find(UUID.fromString(result.getString("acquired_uuid")), connection);
          } else {
            return null;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Collection<UUID> getUsedBy(UUID uuid) {
    Collection<UUID> uuids = new ArrayList<>();
    try (Connection connection = connectionFactory.getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              connectionFactory
                  .statementTickReplacer()
                  .apply("SELECT * FROM 'skins_acquired' WHERE 'acquired_uuid' = ?"))) {
        statement.setString(1, uuid.toString());
        try (ResultSet result = statement.executeQuery()) {
          while (result.next()) {
            uuids.add(UUID.fromString(result.getString("acquirer_uuid")));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return uuids;
  }

  @Override
  public List<StoredSkin> all() {
    List<StoredSkin> list = new ArrayList<>();
    try (Connection connection = connectionFactory.getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              connectionFactory.statementTickReplacer().apply("SELECT * FROM 'skins_storage'"))) {
        try (ResultSet result = statement.executeQuery()) {
          while (result.next()) {
            list.add(
                new StoredSkin(
                    new Skin(
                        UUID.fromString(result.getString("ownerUUID")),
                        result.getString("texture"),
                        result.getString("signature")),
                    result.getString("ownerName")));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
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
