/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)
 * Copyright 2019 contributors

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
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

    private MojangResponse getSkin(
            String name,
            UUID uuid
    ) {
        Optional<MojangResponse> search = knownResponses.stream()
                .filter(response -> response.getNickname().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
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

    private CompletableFuture<MojangResponse> apiFetch(
            String name,
            UUID uuid
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL checkUrl = new URL(String.format(
                        "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                        uuid.toString().replace("-", "")
                ));
                HttpURLConnection connection = (HttpURLConnection) checkUrl.openConnection();
                JsonObject object = new JsonParser()
                        .parse(new InputStreamReader(connection.getInputStream()))
                        .getAsJsonObject();
                if (object.has("error")) {
                    System.err.println("[Skins] The server's being rate limited by mojang api. " +
                            "You may expect some players not having skins.");
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
        UUIDFetcher uuidFetcher = UUIDFetcher.createOrGet(name);
        UUIDFetcher.Callback callback = uuidFetcher.retrieveUUID().join();
        if (callback.getUUID().isPresent()) {
            return getSkin(name, callback.getUUID().get());
        } else {
            return new MojangResponse(name, null, null);
        }
    }

    private boolean contains(MojangResponse response) {
        return knownResponses.stream()
                .anyMatch(storage ->
                        storage.getNickname().toLowerCase().equalsIgnoreCase(
                                response.getNickname().toLowerCase()
                        )
                );
    }
}
