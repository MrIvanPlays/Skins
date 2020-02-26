package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import java.io.File;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public class BukkitSkinsApi extends AbstractSkinsApi {

  private final SkinsBukkit plugin;

  public BukkitSkinsApi(SkinsBukkit plugin, File dataFolder, Logger logger) {
    super(dataFolder, logger);
    this.plugin = plugin;
  }

  @Override
  protected void setNPCSkin(Player player, Skin skin) {
    plugin.getSkinSetter().setSkin(player, skin);
  }
}
