package com.mrivanplays.skins.bukkit.protocolsupport;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.bukkit.core.GeneralBukkitUser;
import com.mrivanplays.skins.bukkit_general.SkinsMenu;
import com.mrivanplays.skins.core.SkinsPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProtocolSupportUser extends GeneralBukkitUser {

  public ProtocolSupportUser(SkinsPlugin plugin, SkinsMenu menuInstance, Player player) {
    super(plugin, menuInstance, player);
  }

  @Override
  public void setNPCSkin(@NotNull Skin skin) {}
}
