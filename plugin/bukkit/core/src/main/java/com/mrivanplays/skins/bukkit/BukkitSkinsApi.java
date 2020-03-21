package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.bukkit.abstraction.handle.SkinSetterHandler;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.io.File;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitSkinsApi extends AbstractSkinsApi {

  public BukkitSkinsApi(
      File dataFolder, Logger logger, Function<SkullItemBuilderData, ItemStack> transformer) {
    super(dataFolder, logger, transformer);
  }

  @Override
  protected void setNPCSkin(Player player, Skin skin) {
    SkinSetterHandler.getSkinSetter().setSkin(player, skin);
  }
}
