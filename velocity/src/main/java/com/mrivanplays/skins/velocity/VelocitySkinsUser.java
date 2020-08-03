package com.mrivanplays.skins.velocity;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.velocitypowered.api.proxy.Player;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public class VelocitySkinsUser extends AbstractSkinsUser {

  private final Player parent;

  public VelocitySkinsUser(Player parent, SkinsPlugin plugin) {
    super(plugin);
    this.parent = parent;
  }

  @Override
  public void setNPCSkin(@NotNull Skin skin) {
    // do nothing - proxies can't have dynamic skin set
  }

  @Override
  public void openSkinMenu() {
    sendMessage(TextComponent.of("The Velocity implementation of Skins does not support skin menu. "));
  }

  @Override
  public @NotNull String getName() {
    return parent.getUsername();
  }

  @Override
  public void sendMessage(Component message) {
    parent.sendMessage(message);
  }

  @Override
  public boolean hasPermission(String permission) {
    return parent.hasPermission(permission);
  }

  @Override
  public @NotNull UUID getUniqueId() {
    return parent.getUniqueId();
  }

  @Override
  public boolean isOnline() {
    return true;
  }
}
