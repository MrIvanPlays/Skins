package com.mrivanplays.skins.api;

import java.util.UUID;
import net.kyori.adventure.text.Component;
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

  void setSkin(@NotNull Skin skin);

  void sendMessage(@NotNull Component message);
}
