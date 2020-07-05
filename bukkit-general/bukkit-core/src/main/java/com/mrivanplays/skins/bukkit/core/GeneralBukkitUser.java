package com.mrivanplays.skins.bukkit.core;

import com.mrivanplays.skins.bukkit_general.SkinsMenu;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsPlugin;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeCordComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class GeneralBukkitUser extends AbstractSkinsUser {

  protected final Player player;
  protected final SkinsMenu menuInstance;

  public GeneralBukkitUser(SkinsPlugin plugin, SkinsMenu menuInstance, Player player) {
    super(plugin);
    this.player = player;
    this.menuInstance = menuInstance;
  }

  @Override
  public void openSkinMenu() {
    menuInstance.openMenu(player);
  }

  @Override
  public @NotNull String getName() {
    return player.getName();
  }

  @Override
  public void sendMessage(Component message) {
    player.spigot().sendMessage(BungeeCordComponentSerializer.get().serialize(message));
  }

  @Override
  public boolean hasPermission(String permission) {
    return player.hasPermission(permission);
  }

  @Override
  public @NotNull UUID getUniqueId() {
    return player.getUniqueId();
  }
}
