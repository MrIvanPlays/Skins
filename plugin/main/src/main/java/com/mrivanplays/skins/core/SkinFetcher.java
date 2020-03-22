package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.MojangResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class SkinFetcher {

  private final List<MojangResponse> knownResponses = new ArrayList<>();
  private final SkinStorage skinStorage;
  private DataProvider dataProvider;

  public SkinFetcher(SkinStorage skinStorage, DataProvider dataProvider) {
    this.skinStorage = skinStorage;
    this.dataProvider = dataProvider;
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

  public DataProvider getDataProvider() {
    return dataProvider;
  }

  public void setDataProvider(DataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  public CompletableFuture<MojangResponse> apiFetch(String name, UUID uuid) {
    return CompletableFuture.supplyAsync(() -> dataProvider.retrieveSkinResponse(name, uuid));
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
    return dataProvider.retrieveUuid(name);
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
