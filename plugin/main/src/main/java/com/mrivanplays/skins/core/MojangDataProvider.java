package com.mrivanplays.skins.core;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MojangDataProvider implements DataProvider {

  private Map<String, UUID> cachedUUIDs;
  private Logger logger;

  public MojangDataProvider(Logger logger) {
    cachedUUIDs = new HashMap<>();
    this.logger = logger;
  }

  @Override
  public @Nullable UUID retrieveUuid(@NotNull String name) {
    Preconditions.checkNotNull(name, "name");
    if (cachedUUIDs.containsKey(name)) {
      return cachedUUIDs.get(name);
    }
    try {
      URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.addRequestProperty("User-Agent", "skins-uuid-fetcher");

      JsonObject object =
          new JsonParser()
              .parse(new InputStreamReader(connection.getInputStream()))
              .getAsJsonObject();
      UUID uuid = getUuid(object.get("id").getAsString());
      cachedUUIDs.put(name, uuid);
      return uuid;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public @NotNull MojangResponse retrieveSkinResponse(@NotNull String name, @NotNull UUID uuid) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(uuid, "uuid");
    try {
      URL checkUrl =
          new URL(
              String.format(
                  "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                  uuid.toString().replace("-", "")));
      HttpURLConnection connection = (HttpURLConnection) checkUrl.openConnection();
      JsonObject object =
          new JsonParser()
              .parse(new InputStreamReader(connection.getInputStream()))
              .getAsJsonObject();
      if (object.has("error")) {
        logger.severe(
            "The server's being rate limited by mojang api. "
                + "You may expect some players not having skins.");
        return new MojangResponse(name, null);
      }
      JsonArray properties = object.get("properties").getAsJsonArray();
      for (JsonElement property : properties) {
        if (!property.isJsonObject()) {
          continue;
        }
        JsonObject propertyObject = property.getAsJsonObject();
        if (!propertyObject.get("name").getAsString().equalsIgnoreCase("textures")) {
          continue;
        }
        String texture = propertyObject.get("value").getAsString();
        String signature = propertyObject.get("signature").getAsString();
        Skin skin = new Skin(uuid, texture, signature);
        return new MojangResponse(name, skin);
      }
    } catch (Exception e) {
      return new MojangResponse(name, null);
    }
    return new MojangResponse(name, null);
  }

  private UUID getUuid(String id) {
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
}
