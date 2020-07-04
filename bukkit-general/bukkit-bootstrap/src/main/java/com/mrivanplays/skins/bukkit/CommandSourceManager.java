package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.bukkit.core.BukkitCommandSource;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.command.CommandSource;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSourceManager {

  private final SkinsPlugin plugin;
  private Map<CommandSender, CommandSource> sourceMap = new HashMap<>();

  public CommandSourceManager(SkinsPlugin plugin) {
    this.plugin = plugin;
  }

  public CommandSource obtain(CommandSender sender) {
    if (sender instanceof Player) {
      return plugin.obtainUser(sender.getName());
    } else {
      if (sourceMap.containsKey(sender)) {
        return sourceMap.get(sender);
      }
      BukkitCommandSource source = new BukkitCommandSource(sender);
      sourceMap.put(sender, source);
      return source;
    }
  }
}
