package com.mrivanplays.skins.bukkit.protocolsupport;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.bukkit.core.GeneralBukkitUser;
import com.mrivanplays.skins.core.SkinsPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProtocolSupportUser extends GeneralBukkitUser {

  public ProtocolSupportUser(SkinsPlugin plugin, Player player) {
    super(plugin, player);
  }

  @Override
  public void setNPCSkin(@NotNull Skin skin) {}
}
