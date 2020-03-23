package com.mrivanplays.skins;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandSkinReload implements CommandExecutor {

  private final SkinsBukkitPlugin plugin;

  public CommandSkinReload(SkinsBukkitPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String s,
      @NotNull String[] args) {
    if (!sender.hasPermission("skins.reload")) {
      sender.sendMessage(plugin.color(plugin.getConfig().getString("messages.no-permission")));
      return true;
    }
    plugin.reload();
    sender.sendMessage(ChatColor.GREEN + "Config reloaded successfully!");
    return true;
  }
}
