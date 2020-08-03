package com.mrivanplays.skins.velocity;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsPlugin;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class VelocitySkinsOfflineUser extends AbstractSkinsUser {

  private String name;
  private UUID uuid;

  public VelocitySkinsOfflineUser(String name, UUID uuid, SkinsPlugin plugin) {
    super(plugin);
    this.name = name;
    this.uuid = uuid;
  }

  @Override
  public void setNPCSkin(@NotNull Skin skin) {}

  @Override
  public void openSkinMenu() {}

  @Override
  public @NotNull String getName() {
    return name;
  }

  @Override
  public void sendMessage(Component message) {
    System.out.println("I'm a fucking offline user");
  }

  @Override
  public boolean hasPermission(String permission) {
    return false;
  }

  @Override
  public @NotNull UUID getUniqueId() {
    return uuid;
  }

  @Override
  public boolean isOnline() {
    return false;
  }
}
