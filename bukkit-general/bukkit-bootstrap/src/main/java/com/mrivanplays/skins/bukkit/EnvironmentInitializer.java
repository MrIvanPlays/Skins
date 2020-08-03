package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Environment;
import com.mrivanplays.skins.bukkit.paper.PaperEnvironment;
import com.mrivanplays.skins.bukkit.protocolsupport.ProtocolSupportEnvironment;
import org.bukkit.Bukkit;

public class EnvironmentInitializer {

  private static Environment env;

  public static Environment get() {
    if (env != null) {
      return env;
    }
    Environment parent;
    try {
      Class.forName("com.destroystokyo.paper.PaperConfig");
      parent = new PaperEnvironment();
    } catch (ClassNotFoundException e) {
      parent = new CraftBukkitEnvironment();
    }
    if (Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")) {
      env = new ProtocolSupportEnvironment(parent.capitalizedName(), parent.paper());
    } else {
      env = parent;
    }
    return env;
  }
}
