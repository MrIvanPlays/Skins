package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.storage.SkinStorage;
import com.mrivanplays.skins.core.storage.StoredSkin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.plugin.Plugin;

public final class SkinFetcher {

  private final List<MojangResponse> knownResponses = new ArrayList<>();
  private final SkinStorage skinStorage;
  private DataProvider dataProvider;

  public SkinFetcher(SkinStorage skinStorage, DataProvider dataProvider) {
    this.skinStorage = skinStorage;
    this.dataProvider = dataProvider;
  }

  public MojangResponse getSkin(String name, UUID uuid) {
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
        return response;
      } else {
        Optional<StoredSkin> storedSkin = skinStorage.getStoredSkin(uuid);
        if (storedSkin.isPresent()) {
          StoredSkin sskin = storedSkin.get();
          MojangResponse mojangResponse = new MojangResponse(name, uuid, sskin.getSkin());
          knownResponses.replaceAll(
              resp -> {
                if (resp.getNickname().equalsIgnoreCase(name)) {
                  return mojangResponse;
                }
                return resp;
              });
          return mojangResponse;
        } else {
          knownResponses.remove(response);
          MojangResponse apiFetch = dataProvider.retrieveSkinResponse(name, uuid);
          knownResponses.add(apiFetch);
          return apiFetch;
        }
      }
    } else {
      MojangResponse response = dataProvider.retrieveSkinResponse(name, uuid);
      knownResponses.add(response);
      return response;
    }
  }

  public List<MojangResponse> getKnownResponses() {
    return knownResponses;
  }

  public void startUpdateCheck(Plugin plugin, int secondsFreq) {
    plugin
        .getServer()
        .getScheduler()
        .runTaskTimerAsynchronously(
            plugin,
            () -> {
              Map<MojangResponse, MojangResponse> replacements = new HashMap<>();
              for (MojangResponse response : knownResponses) {
                MojangResponse apiFetch =
                    dataProvider.retrieveSkinResponse(response.getNickname(), response.getUUID());
                Optional<Skin> skinOptional = apiFetch.getSkin();
                if (!skinOptional.isPresent()) {
                  continue;
                }
                replacements.put(response, apiFetch);
              }
              try {
                for (Map.Entry<MojangResponse, MojangResponse> entry : replacements.entrySet()) {
                  knownResponses.remove(entry.getKey());
                  knownResponses.add(entry.getValue());
                }
              } finally {
                replacements.clear();
              }
            },
            0,
            secondsFreq * 20);
  }

  public DataProvider getDataProvider() {
    return dataProvider;
  }

  public void setDataProvider(DataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  public MojangResponse getSkin(String name) {
    return getSkin(name, fetchUUID(name));
  }

  public UUID fetchUUID(String name) {
    return dataProvider.retrieveUuid(name);
  }
}
