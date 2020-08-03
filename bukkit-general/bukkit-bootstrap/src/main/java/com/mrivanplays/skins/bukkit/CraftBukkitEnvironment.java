package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Environment;
import org.bukkit.Bukkit;

public class CraftBukkitEnvironment extends Environment {

  @Override
  public boolean proxy() {
    return false;
  }

  @Override
  public boolean dynamicSkinSetSupported() {
    return false;
  }

  @Override
  public boolean protocolSupport() {
    return false;
  }

  @Override
  public boolean paper() {
    return false;
  }

  @Override
  protected String getCapitalizedName() {
    return "Bukkit - " + (isSpigot() ? "Spigot" : Bukkit.getName());
  }

  private boolean isSpigot() {
    try {
      Class.forName("org.spigotmc.SpigotConfig");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
