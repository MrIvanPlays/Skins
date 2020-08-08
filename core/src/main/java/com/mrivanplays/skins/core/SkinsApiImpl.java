package com.mrivanplays.skins.core;

import com.google.common.base.Preconditions;
import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.api.SkinsInfo;
import com.mrivanplays.skins.api.User;
import com.mrivanplays.skins.core.util.Utils;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
  public CompletableFuture<Optional<Skin>> getSkin(@NotNull String name) {
    Preconditions.checkNotNull(name, "name");
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
  public @NotNull User getUser(@NotNull String name) {
    return plugin.obtainUser(name);
  }

  @Override
  public @NotNull User getUser(@NotNull UUID uuid) {
    return plugin.obtainUser(uuid);
  }

  @Override
  public @NotNull CompletableFuture<Collection<User>> getUsedBy(@NotNull Skin skin) {
    return plugin
        .getStorage()
        .getUsedBy(skin.getOwner())
        .thenApplyAsync(list -> Utils.map(list, this::getUser), plugin.getScheduler().async());
  }

  public SkinAccessor getSkinAccessor() {
    return skinAccessor;
  }
}
