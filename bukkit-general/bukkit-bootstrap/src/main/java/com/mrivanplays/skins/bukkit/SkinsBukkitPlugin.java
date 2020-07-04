package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Environment;
import com.mrivanplays.skins.core.AbstractSkinsPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SkinsBukkitPlugin extends JavaPlugin {

  private AbstractSkinsPlugin plugin;
  private boolean disabled = false;

  @Override
  public void onLoad() {
    // todo: check if supported version, disable if not supported
    plugin = new BukkitSkinsPlugin(this);
    plugin.enable();
  }

  @Override
  public void onDisable() {
    plugin.disable();
  }

  @Override
  public void onEnable() {
    if (disabled) {
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    Environment env = plugin.getInfo().getEnvironment();
    if (!env.paper()) {
      getLogger().severe("Skins works better if ran on paper.");
    }
    if (env.protocolSupport()) {

    }
    getLogger().info("Running on " + env.capitalizedName());
  }
}
