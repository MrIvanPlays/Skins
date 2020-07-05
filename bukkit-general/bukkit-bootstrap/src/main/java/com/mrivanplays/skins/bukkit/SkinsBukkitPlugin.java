package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Environment;
import com.mrivanplays.skins.bukkit.protocolsupport.ProtocolSupportSkinSetter;
import com.mrivanplays.skins.bukkit_general.skull_skinner.SupportedVersions;
import com.mrivanplays.skins.core.AbstractSkinsPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SkinsBukkitPlugin extends JavaPlugin {

  private AbstractSkinsPlugin plugin;
  private boolean disabled = false;

  @Override
  public void onLoad() {
    if (!SupportedVersions.isCurrentSupported()) {
      getLogger().severe("Detected unsupported version, disabling...");
      disabled = true;
      return;
    }
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
      getServer().getPluginManager().registerEvents(new ProtocolSupportSkinSetter(plugin), this);
    } else {
      getServer().getPluginManager().registerEvents(new DefaultSkinSetListener(plugin), this);
    }

    getLogger().info("Running on " + env.capitalizedName());
  }
}
