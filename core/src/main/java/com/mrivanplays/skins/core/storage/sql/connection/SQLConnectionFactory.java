package com.mrivanplays.skins.core.storage.sql.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public interface SQLConnectionFactory {

  void connect() throws SQLException;

  Connection getConnection() throws SQLException;

  Function<String, String> statementTickReplacer();

  void close() throws SQLException;
}
