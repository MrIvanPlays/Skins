package com.mrivanplays.skins.bukkit.core;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderApplier {

  public static String applyPlaceholders(Player player, String message) {
    return PlaceholderAPI.setPlaceholders(player, message);
  }
}
