package com.mrivanplays.skins.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

class UUIDFetcher {

  private final String playerName;
  private Callback lastResult;

  //

  private static final Map<String, UUIDFetcher> knownInstances = new ConcurrentHashMap<>();

  static UUIDFetcher createOrGet(String playerName) {
    if (knownInstances.get(playerName) != null) {
      return knownInstances.get(playerName);
    } else {
      UUIDFetcher fetcher = new UUIDFetcher(playerName);
      knownInstances.put(playerName, fetcher);
      return fetcher;
    }
  }

  //

  private UUIDFetcher(String playerName) {
    this.playerName = playerName;
  }

  CompletableFuture<Callback> retrieveUUID() {
    if (lastResult != null) {
      return CompletableFuture.completedFuture(lastResult);
    }
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "MrIvanPlays-UUID-Fetcher");

            JsonElement element =
                new JsonParser().parse(new InputStreamReader(connection.getInputStream()));
            if (!element.isJsonObject()) {
              return new Callback(null);
            }
            JsonObject object = element.getAsJsonObject();
            return new Callback(get(object.get("id").getAsString()));
          } catch (IOException e) {
            return new Callback(null);
          }
        });
  }

  private UUID get(String id) {
    return UUID.fromString(
        id.substring(0, 8)
            + "-"
            + id.substring(8, 12)
            + "-"
            + id.substring(12, 16)
            + "-"
            + id.substring(16, 20)
            + "-"
            + id.substring(20, 32));
  }

  class Callback {

    {
      lastResult = this;
    }

    private final Optional<UUID> uuid;

    public Callback(UUID uuid) {
      this.uuid = Optional.ofNullable(uuid);
    }

    public Optional<UUID> getUUID() {
      return uuid;
    }
  }
}
