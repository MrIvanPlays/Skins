package com.mrivanplays.skins.bukkit.core;

import com.mrivanplays.skins.core.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeCordComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitCommandSource implements CommandSource {

  private final CommandSender sender;

  public BukkitCommandSource(CommandSender sender) {
    this.sender = sender;
  }

  @Override
  public String getName() {
    return sender.getName();
  }

  @Override
  public void sendMessage(Component message) {
    sender.spigot().sendMessage(BungeeCordComponentSerializer.get().serialize(message));
  }

  @Override
  public boolean hasPermission(String permission) {
    return sender.hasPermission(permission);
  }

  @Override
  public boolean isPlayer() {
    return sender instanceof Player;
  }
}
