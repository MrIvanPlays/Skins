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
              plugin.getScheduler().sync().execute(() -> updateCurrentStoredSkin(storedSkin));
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
        .getSkin(getOriginalUniqueId(), true)
        .thenApplyAsync(Optional::ofNullable, plugin.getScheduler().async());
  }

  @Override
  public void setSkin(@NotNull Skin skin) {
    plugin
        .getScheduler()
        .async()
        .execute(
            () ->
                setSkin(
                    skin,
                    apiImpl.getSkinAccessor().getDataProvider().retrieveName(skin.getOwner()),
                    true));
  }

  public void setSkin(Skin skin, String name, boolean fromApi) {
    Objects.requireNonNull(skin, "skin");
    Objects.requireNonNull(name, "name");
    CompletableFuture<StoredSkin> future = plugin.getStorage().find(skin.getOwner());
    if (fromApi) {
      future.thenAccept(storedSkin -> modify(storedSkin, skin, name));
    } else {
      future.thenAcceptAsync(
          storedSkin -> modify(storedSkin, skin, name), plugin.getScheduler().async());
    }
  }

  private void modify(StoredSkin storedSkin, Skin skin, String name) {
    boolean justCreated = false;
    if (storedSkin == null) {
      storedSkin = new StoredSkin(skin, name);
      justCreated = true;
    }

    if (currentStoredSkin != null) {
      if (!currentStoredSkin.equalsNoSkin(storedSkin)) {
        plugin.getStorage().setAcquired(getUniqueId(), skin.getOwner());
      }
    } else {
      StoredSkin otherStored = plugin.getStorage().acquired(getUniqueId()).join();
      if (otherStored != null) {
        if (!otherStored.equalsNoSkin(storedSkin)) {
          plugin.getStorage().setAcquired(getUniqueId(), skin.getOwner());
        }
      } else {
        plugin.getStorage().setAcquired(getUniqueId(), skin.getOwner());
      }
    }
    Skin newSkin = apiImpl.getSkinAccessor().getSkin(skin.getOwner(), false).join();
    // uh oh spaghettio
    // im a pasta master :P
    // on a serious note, idk how should I replace this so it'll stay for now
    if (newSkin != null && storedSkin.getSkin() != null && !storedSkin.getSkin().equals(newSkin)) {
      storedSkin.setSkin(newSkin);
      StoredSkin finalStoredSkin = storedSkin;
      plugin
          .getStorage()
          .storeSkin(storedSkin)
          .thenAccept(($) -> callScheduler(finalStoredSkin, newSkin));
    } else if (newSkin == null && storedSkin.getSkin() == null) {
      storedSkin.setSkin(skin);
      callScheduler(storedSkin, skin);
    } else if (newSkin != null
        && storedSkin.getSkin() != null
        && storedSkin.getSkin().equals(newSkin)) {
      callScheduler(storedSkin, newSkin);
    } else if (newSkin != null && storedSkin.getSkin() == null) {
      storedSkin.setSkin(newSkin);
      callScheduler(storedSkin, newSkin);
    } else if (newSkin == null && storedSkin.getSkin() != null) {
      callScheduler(storedSkin, skin);
    }

    if (justCreated) {
      plugin.getStorage().storeSkin(storedSkin);
    }
  }

  private void callScheduler(StoredSkin finalStoredSkin, Skin skin) {
    plugin
        .getScheduler()
        .sync()
        .execute(
            () -> {
              updateCurrentStoredSkin(finalStoredSkin);
              setNPCSkin(skin);
            });
  }

  public abstract void setNPCSkin(@NotNull Skin skin);
}
