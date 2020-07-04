package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApiProvider;
import com.mrivanplays.skins.core.storage.StoredSkin;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSkinsUser implements SkinsUser {

  protected StoredSkin currentStoredSkin;

  protected SkinsPlugin plugin;
  protected SkinsApiImpl apiImpl;

  public AbstractSkinsUser(SkinsPlugin plugin) {
    this.plugin = plugin;
    this.apiImpl = (SkinsApiImpl) SkinsApiProvider.get();
  }

  @Override
  public boolean isPlayer() {
    return true;
  }

  @Override
  public CompletableFuture<Optional<Skin>> getSkin() {
    if (currentStoredSkin != null) {
      return CompletableFuture.completedFuture(Optional.of(currentStoredSkin.getSkin()));
    }
    return plugin
        .getStorage()
        .acquired(getUniqueId())
        .thenApplyAsync(
            storedSkin -> {
              if (storedSkin == null) {
                return getOriginalSkin().join();
              }
              updateCurrentStoredSkin(storedSkin);
              return Optional.of(storedSkin.getSkin());
            },
            plugin.getScheduler().async());
  }

  private void updateCurrentStoredSkin(StoredSkin newStoredSkin) {
    this.currentStoredSkin = newStoredSkin;
  }

  @Override
  public @Nullable UUID getOriginalUniqueId() {
    return apiImpl.getSkinAccessor().getDataProvider().retrieveUuid(getName());
  }

  @Override
  public CompletableFuture<Optional<Skin>> getOriginalSkin() {
    return apiImpl
        .getSkinAccessor()
        .getSkin(getOriginalUniqueId())
        .thenApplyAsync(Optional::ofNullable, plugin.getScheduler().async());
  }

  @Override
  public void setSkin(@NotNull Skin skin) {
    Objects.requireNonNull(skin, "skin");
    if (currentStoredSkin != null) {
      currentStoredSkin.removeAcquirer(getUniqueId());
      plugin.getStorage().storeSkin(currentStoredSkin);
    }
    plugin
        .getStorage()
        .find(skin.getOwner())
        .thenAccept(
            storedSkin -> {
              if (storedSkin == null) {
                storedSkin =
                    new StoredSkin(
                        skin,
                        apiImpl.getSkinAccessor().getDataProvider().retrieveName(skin.getOwner()));
              }
              storedSkin.addAcquirer(getUniqueId());
              Skin newSkin = apiImpl.getSkinAccessor().getSkin(skin.getOwner()).join();
              if (newSkin != null) {
                storedSkin.setSkin(newSkin);
              }

              plugin.getStorage().storeSkin(storedSkin);
              updateCurrentStoredSkin(storedSkin);

              Skin setSkin;
              if (newSkin != null) {
                setSkin = newSkin;
              } else {
                setSkin = skin;
              }
              setNPCSkin(setSkin);
            });
  }

  public abstract void setNPCSkin(@NotNull Skin skin);
}
