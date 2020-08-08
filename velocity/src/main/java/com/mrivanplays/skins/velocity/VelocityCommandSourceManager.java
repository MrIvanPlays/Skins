package com.mrivanplays.skins.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import java.util.HashMap;
import java.util.Map;

public class VelocityCommandSourceManager {

  private final VelocitySkinsPlugin plugin;

  private Map<CommandSource, com.mrivanplays.skins.core.command.CommandSource> sourceMap =
      new HashMap<>();

  public VelocityCommandSourceManager(VelocitySkinsPlugin plugin) {
    this.plugin = plugin;
  }

  public com.mrivanplays.skins.core.command.CommandSource obtainSource(CommandSource parent) {
    if (parent instanceof Player) {
      return plugin.obtainUser(((Player) parent).getUniqueId());
    } else {
      if (sourceMap.containsKey(parent)) {
        return sourceMap.get(parent);
      }
      VelocityCommandSource source = new VelocityCommandSource(parent);
      sourceMap.put(parent, source);
      return source;
    }
  }
}
