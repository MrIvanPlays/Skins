package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.api.SkinsInfo;
import com.mrivanplays.skins.api.User;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class SkinsApiImpl implements SkinsApi {

  private final SkinsPlugin plugin;
  private final SkinAccessor skinAccessor;

  public SkinsApiImpl(SkinsPlugin plugin) {
    this.plugin = plugin;
    this.skinAccessor =
        new SkinAccessor(
            new MojangDataProvider(plugin.getLogger()), plugin.getScheduler(), plugin.getStorage());
  }

  @Override
  public CompletableFuture<Optional<Skin>> getSkin(String name) {
    return skinAccessor.getSkin(name).thenApplyAsync(Optional::ofNullable);
  }

  @Override
  public void setDataProvider(@NotNull DataProvider dataProvider) {
    skinAccessor.setDataProvider(dataProvider);
  }

  @Override
  public @NotNull SkinsInfo getInfo() {
    return plugin.getInfo();
  }

  @Override
  public @NotNull CompletableFuture<User> getUser(@NotNull String name) {
    return plugin.obtainUser(name).thenApplyAsync(SkinsUser::toUser);
  }

  @Override
  public @NotNull CompletableFuture<User> getUser(@NotNull UUID uuid) {
    return plugin.obtainUser(uuid).thenApplyAsync(SkinsUser::toUser);
  }

  @Override
  public @NotNull CompletableFuture<Collection<User>> getUsedBy(@NotNull Skin skin) {
    return plugin
        .getStorage()
        .all()
        .thenApplyAsync(
            list ->
                list.stream()
                    .filter(storedSkin -> storedSkin.getSkin().equals(skin))
                    .map(storedSkin -> getUser(storedSkin.getSkin().getOwner()).join())
                    .collect(Collectors.toList()),
            plugin.getScheduler().async());
  }

  public SkinAccessor getSkinAccessor() {
    return skinAccessor;
  }
}
