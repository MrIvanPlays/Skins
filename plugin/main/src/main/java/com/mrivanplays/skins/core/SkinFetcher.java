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
import com.mrivanplays.skins.api.Skin;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SkinFetcher {

    private final Set<Skin> knownSkins = new HashSet<>();

    private Skin getSkin(UUID uuid) {
        Optional<Skin> search = knownSkins.stream()
                .filter(skin -> skin.getOwner().equals(uuid))
                .findFirst();
        if (search.isPresent()) {
            return search.get();
        } else {
            try {
                URL checkUrl = new URL(String.format(
                        "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                        uuid.toString().replace("-", "")
                ));
                HttpURLConnection connection = (HttpURLConnection) checkUrl.openConnection();
                JsonObject object = new JsonParser()
                        .parse(new InputStreamReader(connection.getInputStream()))
                        .getAsJsonObject();
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
                    knownSkins.add(skin);
                    return skin;
                }
            } catch (Exception e) {
                return new Skin(uuid, "exception", e.fillInStackTrace().toString());
            }
        }
        return null;
    }

    public Skin getSkin(String name) {
        UUIDFetcher uuidFetcher = UUIDFetcher.createOrGet(name);
        UUIDFetcher.Callback callback = uuidFetcher.retrieveUUID().join();
        if (callback.getUUID().isPresent()) {
            return getSkin(callback.getUUID().get());
        } else {
            return new Skin(UUID.randomUUID(), "exception", "could not get uuid");
        }
    }
}
