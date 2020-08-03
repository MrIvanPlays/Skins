package com.mrivanplays.skins.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public class VelocityCommandSource implements com.mrivanplays.skins.core.command.CommandSource {

  private final CommandSource parent;
  private VelocitySkinsPlugin plugin;

  public VelocityCommandSource(CommandSource parent, VelocitySkinsPlugin plugin) {
    this.parent = parent;
    this.plugin = plugin;
  }

  @Override
  public String getName() {
    return isPlayer() ? ((Player) parent).getUsername() : "CONSOLE";
  }

  @Override
  public void sendMessage(Component message) {
    plugin.getScheduler().sync().execute(() -> parent.sendMessage(message));
  }

  @Override
  public boolean hasPermission(String permission) {
    return parent.hasPermission(permission);
  }

  @Override
  public boolean isPlayer() {
    return parent instanceof Player;
  }
}
