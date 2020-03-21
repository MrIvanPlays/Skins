package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.bukkit.abstraction.handle.SkinSetterHandler;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.InitializationData;
import org.bukkit.entity.Player;

public class BukkitSkinsApi extends AbstractSkinsApi {

  public BukkitSkinsApi(InitializationData initializationData) {
    super(initializationData);
  }

  @Override
  protected void setNPCSkin(Player player, Skin skin) {
    SkinSetterHandler.getSkinSetter().setSkin(player, skin);
  }
}
