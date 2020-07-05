package com.mrivanplays.skins.bukkit.core;

import com.mrivanplays.skins.bukkit_general.SkinsMenu;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsPlugin;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeCordComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class GeneralBukkitUser extends AbstractSkinsUser {

  protected final OfflinePlayer player;
  protected final SkinsMenu menuInstance;

  public GeneralBukkitUser(SkinsPlugin plugin, SkinsMenu menuInstance, OfflinePlayer player) {
    super(plugin);
    this.player = player;
    this.menuInstance = menuInstance;
  }

  @Override
  public void openSkinMenu() {
    if (isOnline()) {
      menuInstance.openMenu(getOnlineVariant());
    }
  }

  @Override
  public @NotNull String getName() {
    return player.getName();
  }

  @Override
  public boolean isOnline() {
    return getOnlineVariant() != null;
  }

  public Player getOnlineVariant() {
    return player.getPlayer();
  }

  @Override
  public void sendMessage(Component message) {
    if (isOnline()) {
      getOnlineVariant()
          .spigot()
          .sendMessage(BungeeCordComponentSerializer.get().serialize(message));
    }
  }

  @Override
  public boolean hasPermission(String permission) {
    if (isOnline()) {
      return getOnlineVariant().hasPermission(permission);
    } else {
      return false;
    }
  }

  @Override
  public @NotNull UUID getUniqueId() {
    return player.getUniqueId();
  }
}
