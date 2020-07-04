package com.mrivanplays.skins.api;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface User {

  @NotNull
  String getName();

  @NotNull
  UUID getUniqueId();

  @Nullable
  UUID getOriginalUniqueId();

  CompletableFuture<Optional<Skin>> getSkin();

  CompletableFuture<Optional<Skin>> getOriginalSkin();

  void setSkin(@NotNull Skin skin);
}
