package com.mrivanplays.skins;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandSkinReload implements TabExecutor {

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

  @Override
  public @Nullable List<String> onTabComplete(
      @NotNull CommandSender commandSender,
      @NotNull Command command,
      @NotNull String s,
      @NotNull String[] strings) {
    return Collections.emptyList();
  }
}
