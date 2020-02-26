package com.mrivanplays.skins;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandSkinMenu implements TabExecutor {

  private SkinsBukkitPlugin plugin;
  private Map<UUID, Long> cooldowns = new HashMap<>();

  public CommandSkinMenu(SkinsBukkitPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(plugin.color(plugin.getConfig().getString("messages.no-console")));
      return true;
    }
    Player player = (Player) sender;
    if (!player.hasPermission("skins.menu")) {
      player.sendMessage(plugin.color(plugin.getConfig().getString("messages.no-permission")));
      return true;
    }
    if (cooldowns.containsKey(player.getUniqueId())) {
      int cooldown = 60000;
      long lastExecuted = cooldowns.get(player.getUniqueId());
      long now = System.currentTimeMillis();
      if ((now - lastExecuted) < cooldown) {
        player.sendMessage(
            plugin.color(
            plugin
                .getConfig()
                .getString("messages.cooldown")
                .replace("%timeLeft%", String.valueOf((cooldown - (now - lastExecuted)) / 1000))));
        return true;
      }
    }
    plugin.getSkinsMenu().openMenu(player);
    cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String alias,
      @NotNull String[] args) {
    return Collections.emptyList();
  }
}
