package com.mrivanplays.skins.bukkit.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.bukkit.core.GeneralBukkitUser;
import com.mrivanplays.skins.bukkit_general.SkinsMenu;
import com.mrivanplays.skins.core.SkinsPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaperUser extends GeneralBukkitUser {

  public PaperUser(SkinsPlugin plugin, SkinsMenu menuInstance, OfflinePlayer player) {
    super(plugin, menuInstance, player);
  }

  @Override
  public void setNPCSkin(@NotNull Skin skin) {
    if (!isOnline()) {
      return;
    }
    Player online = getOnlineVariant();
    PlayerProfile profile = online.getPlayerProfile();
    profile.setProperty(new ProfileProperty("textures", skin.getTexture(), skin.getSignature()));
    online.setPlayerProfile(profile);
  }
}
