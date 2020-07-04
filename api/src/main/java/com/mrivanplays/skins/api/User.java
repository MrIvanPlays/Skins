package com.mrivanplays.skins.api;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface User {

  @NotNull
  String getName();

  @NotNull
  UUID getUniqueId();

  @Nullable
  UUID getOriginalUniqueId();

  @Nullable
  Skin getSkin();

  @Nullable
  Skin getOriginalSkin();

  void setSkin(@NotNull Skin skin);
}
