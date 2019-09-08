/*
    Copyright (C) 2019 Ivan Pekov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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

public class SkinFetcher {

  private final List<MojangResponse> knownResponses = new ArrayList<>();
  private final SkinStorage skinStorage;

  public SkinFetcher(SkinStorage skinStorage) {
    this.skinStorage = skinStorage;
  }

  private MojangResponse getSkin(String name, UUID uuid) {
    Optional<MojangResponse> search =
        knownResponses.stream()
            .filter(
                response ->
                    response.getNickname().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
            .findFirst();
    if (search.isPresent()) {
      MojangResponse response = search.get();
      if (response.getSkin().isPresent()) {
        return response;
      } else {
        Optional<StoredSkin> storedSkin = skinStorage.getStoredSkin(uuid);
        if (storedSkin.isPresent()) {
          StoredSkin sskin = storedSkin.get();
          MojangResponse mojangResponse = new MojangResponse(name, uuid, sskin.getSkin());
          if (!contains(mojangResponse)) {
            knownResponses.add(mojangResponse);
          }
          return mojangResponse;
        } else {
          knownResponses.remove(response);
          MojangResponse apiFetch = apiFetch(name, uuid).join();
          if (!contains(apiFetch)) {
            knownResponses.add(apiFetch);
          }
          return apiFetch;
        }
      }
    } else {
      MojangResponse response = apiFetch(name, uuid).join();
      if (!contains(response)) {
        knownResponses.add(response);
      }
      return response;
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
              System.err.println(
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

  public MojangResponse getSkin(String name) {
    UUID fetchedUUID = fetchUUID(name);
    if (fetchedUUID != null) {
      return getSkin(name, fetchedUUID);
    } else {
      return new MojangResponse(name, null, null);
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

  public CompletableFuture<String> fetchName(UUID uuid) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            URL url =
                new URL(
                    "https://api.mojang.com/user/profiles/"
                        + uuid.toString().replace("-", "")
                        + "/names");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            JsonArray names =
                new JsonParser()
                    .parse(new InputStreamReader(connection.getInputStream()))
                    .getAsJsonArray();
            JsonObject lastName = names.get(names.size() - 1).getAsJsonObject();
            return lastName.get("name").getAsString();
          } catch (Exception e) {
            return "";
          }
        });
  }

  private boolean contains(MojangResponse response) {
    return knownResponses.stream()
        .anyMatch(
            storage ->
                storage
                    .getNickname()
                    .toLowerCase()
                    .equalsIgnoreCase(response.getNickname().toLowerCase()));
  }
}
