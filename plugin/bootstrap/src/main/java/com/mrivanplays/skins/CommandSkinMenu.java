package com.mrivanplays.skins;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandSkinMenu implements CommandExecutor {

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
                    .replace(
                        "%timeLeft%", String.valueOf((cooldown - (now - lastExecuted)) / 1000))));
        return true;
      }
    }
    plugin.getSkinsMenu().openMenu(player);
    cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    return true;
  }
}
