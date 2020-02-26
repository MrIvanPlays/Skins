package com.mrivanplays.skins.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class SkinFetcher {

  private final List<MojangResponse> knownResponses = new ArrayList<>();
  private final SkinStorage skinStorage;
  private Logger logger;

  public SkinFetcher(SkinStorage skinStorage, Logger logger) {
    this.skinStorage = skinStorage;
    this.logger = logger;
  }

  public MojangResponseHolder getSkin(String name, UUID uuid) {
    Optional<MojangResponse> search =
        knownResponses.stream()
            .filter(
                response ->
                    response.getUuid().isPresent()
                        ? response.getUuid().get().equals(uuid)
                        : response.getNickname().equalsIgnoreCase(name))
            .findFirst();
    if (search.isPresent()) {
      MojangResponse response = search.get();
      if (response.getSkin().isPresent()) {
        return new MojangResponseHolder(response, false);
      } else {
        Optional<StoredSkin> storedSkin = skinStorage.getStoredSkin(uuid);
        if (storedSkin.isPresent()) {
          StoredSkin sskin = storedSkin.get();
          MojangResponse mojangResponse = new MojangResponse(name, uuid, sskin.getSkin());
          if (!contains(mojangResponse)) {
            knownResponses.add(mojangResponse);
          }
          return new MojangResponseHolder(mojangResponse, false);
        } else {
          knownResponses.remove(response);
          MojangResponse apiFetch = apiFetch(name, uuid).join();
          if (!contains(apiFetch)) {
            knownResponses.add(apiFetch);
          }
          return new MojangResponseHolder(apiFetch, true);
        }
      }
    } else {
      MojangResponse response = apiFetch(name, uuid).join();
      if (!contains(response)) {
        knownResponses.add(response);
      }
      return new MojangResponseHolder(response, true);
    }
  }

  public CompletableFuture<MojangResponse> apiFetch(String name, UUID uuid) {
    return CompletableFuture.supplyAsync(
        () -> {
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
                  "[Skins] The server's being rate limited by mojang api. "
                      + "You may expect some players not having skins.");
              return new MojangResponse(name, uuid, null);
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
              return new MojangResponse(name, uuid, skin);
            }
          } catch (Exception e) {
            return new MojangResponse(name, uuid, null);
          }
          return new MojangResponse(name, uuid, null);
        });
  }

  public MojangResponseHolder getSkin(String name) {
    UUID fetchedUUID = fetchUUID(name);
    if (fetchedUUID != null) {
      return getSkin(name, fetchedUUID);
    } else {
      return new MojangResponseHolder(new MojangResponse(name, null, null), false);
    }
  }

  public UUID fetchUUID(String name) {
    UUIDFetcher uuidFetcher = UUIDFetcher.createOrGet(name);
    UUIDFetcher.Callback callback = uuidFetcher.retrieveUUID().join();
    if (callback.getUUID().isPresent()) {
      return callback.getUUID().get();
    }
    return null;
  }

  private boolean contains(MojangResponse response) {
    return knownResponses.stream()
        .anyMatch(
            storage -> {
              if (storage.getUuid().isPresent() && response.getUuid().isPresent()) {
                return storage.getUuid().get().equals(response.getUuid().get());
              } else {
                return storage.getNickname().equalsIgnoreCase(response.getNickname());
              }
            });
  }
}
