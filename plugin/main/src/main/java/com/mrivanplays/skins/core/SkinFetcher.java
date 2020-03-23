package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SkinFetcher {

  private final List<MojangResponse> knownResponses = new ArrayList<>();
  private final Map<UUID, OffsetDateTime> fetchedAt = new HashMap<>();
  private final SkinStorage skinStorage;
  private DataProvider dataProvider;

  public SkinFetcher(SkinStorage skinStorage, DataProvider dataProvider) {
    this.skinStorage = skinStorage;
    this.dataProvider = dataProvider;
  }

  public MojangResponse getSkin(String name, UUID uuid) {
    Optional<MojangResponse> search =
        knownResponses.stream()
            .filter(response -> response.getNickname().equalsIgnoreCase(name))
            .findFirst();
    if (search.isPresent()) {
      MojangResponse response = search.get();
      if (response.getSkin().isPresent()) {
        Skin skin = response.getSkin().get();
        Skin updated = checkForSkinUpdate(skin, name);
        if (!skin.equals(updated)) {
          MojangResponse newResponse = new MojangResponse(name, updated);
          knownResponses.remove(response);
          knownResponses.add(newResponse);
          Optional<StoredSkin> storedSkinOptional = skinStorage.getStoredSkin(uuid);
          if (storedSkinOptional.isPresent()) {
            StoredSkin storedSkin = storedSkinOptional.get().duplicate();
            storedSkin.setSkin(updated);
            skinStorage.modifySkin(storedSkin);
          } else {
            handleStorage(newResponse);
          }
          return newResponse;
        } else {
          return response;
        }
      } else {
        knownResponses.remove(response);
        Optional<StoredSkin> storedSkinOptional = skinStorage.getStoredSkin(uuid);
        if (storedSkinOptional.isPresent()) {
          StoredSkin storedSkin = storedSkinOptional.get();
          Skin updated = checkForSkinUpdate(storedSkin.getSkin(), name);
          if (!storedSkin.getSkin().equals(updated)) {
            StoredSkin dup = storedSkin.duplicate();
            dup.setSkin(updated);
            skinStorage.modifySkin(dup);
            MojangResponse resp = new MojangResponse(name, updated);
            knownResponses.add(resp);
            return resp;
          } else {
            MojangResponse resp = new MojangResponse(name, storedSkin.getSkin());
            knownResponses.add(resp);
            return resp;
          }
        } else {
          MojangResponse apiFetch = apiFetch(name, uuid).join();
          handleFetchedAt(uuid, OffsetDateTime.now());
          knownResponses.add(apiFetch);
          handleStorage(apiFetch);
          return apiFetch;
        }
      }
    } else {
      Optional<StoredSkin> storedSkinOptional = skinStorage.getStoredSkin(uuid);
      if (storedSkinOptional.isPresent()) {
        StoredSkin storedSkin = storedSkinOptional.get();
        Skin updated = checkForSkinUpdate(storedSkin.getSkin(), name);
        if (!storedSkin.getSkin().equals(updated)) {
          StoredSkin dup = storedSkin.duplicate();
          dup.setSkin(updated);
          skinStorage.modifySkin(dup);
          MojangResponse response = new MojangResponse(name, updated);
          knownResponses.add(response);
          return response;
        } else {
          MojangResponse response = new MojangResponse(name, storedSkin.getSkin());
          knownResponses.add(response);
          return response;
        }
      } else {
        MojangResponse response = apiFetch(name, uuid).join();
        handleFetchedAt(uuid, OffsetDateTime.now());
        knownResponses.add(response);
        handleStorage(response);
        return response;
      }
    }
  }

  private void handleStorage(MojangResponse apiFetch) {
    if (apiFetch.getSkin().isPresent()) {
      Set<String> keys = skinStorage.getKeys();
      OptionalInt biggestNumberStored = keys.stream().mapToInt(Integer::parseInt).max();
      int biggestNumber;
      if (!biggestNumberStored.isPresent()) {
        biggestNumber = 0;
      } else {
        biggestNumber = biggestNumberStored.getAsInt();
      }
      keys.clear();
      StoredSkin skinStored =
          new StoredSkin(
              apiFetch.getSkin().get(),
              Integer.toString(biggestNumber + 1),
              apiFetch.getNickname());
      skinStorage.putFullSkin(skinStored);
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

  public MojangResponse getSkin(String name) {
    UUID fetchedUUID = dataProvider.retrieveUuid(name);
    if (fetchedUUID != null) {
      return getSkin(name, fetchedUUID);
    } else {
      return new MojangResponse(name, null);
    }
  }

  private Skin checkForSkinUpdate(Skin skin, String name) {
    if (fetchedAt.containsKey(skin.getOwner())) {
      Duration duration = Duration.between(fetchedAt.get(skin.getOwner()), OffsetDateTime.now());
      if (duration.getSeconds() < 60) {
        return skin;
      }
    }
    Optional<MojangResponse> responseOptional =
        knownResponses.stream()
            .filter(response -> response.getNickname().equalsIgnoreCase(name))
            .findFirst();
    if (responseOptional.isPresent()) {
      MojangResponse response = responseOptional.get();
      if (response.getSkin().isPresent()) {
        Skin responseSkin = response.getSkin().get();
        if (skin.equals(responseSkin)) {
          MojangResponse apiFetch = apiFetch(name, skin.getOwner()).join();
          handleFetchedAt(skin.getOwner(), OffsetDateTime.now());
          if (apiFetch.getSkin().isPresent()) {
            Skin justFetched = apiFetch.getSkin().get();
            if (justFetched.equals(skin)) {
              return skin;
            } else {
              knownResponses.remove(response);
              knownResponses.add(apiFetch);
              return justFetched;
            }
          } else {
            return responseSkin;
          }
        } else {
          MojangResponse apiFetch = apiFetch(name, skin.getOwner()).join();
          handleFetchedAt(skin.getOwner(), OffsetDateTime.now());
          if (apiFetch.getSkin().isPresent()) {
            knownResponses.remove(response);
            knownResponses.add(apiFetch);
            return apiFetch.getSkin().get();
          } else {
            return responseSkin;
          }
        }
      } else {
        MojangResponse apiFetch = apiFetch(name, skin.getOwner()).join();
        handleFetchedAt(skin.getOwner(), OffsetDateTime.now());
        if (apiFetch.getSkin().isPresent()) {
          knownResponses.remove(response);
          knownResponses.add(apiFetch);
          return apiFetch.getSkin().get();
        } else {
          return skin;
        }
      }
    } else {
      MojangResponse apiFetch = apiFetch(name, skin.getOwner()).join();
      handleFetchedAt(skin.getOwner(), OffsetDateTime.now());
      if (apiFetch.getSkin().isPresent()) {
        knownResponses.add(apiFetch);
        return apiFetch.getSkin().get();
      } else {
        return skin;
      }
    }
  }

  private void handleFetchedAt(UUID uuid, OffsetDateTime time) {
    if (fetchedAt.containsKey(uuid)) {
      fetchedAt.replace(uuid, time);
    } else {
      fetchedAt.put(uuid, time);
    }
  }
}
