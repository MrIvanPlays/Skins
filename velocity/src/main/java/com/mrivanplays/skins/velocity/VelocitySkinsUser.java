package com.mrivanplays.skins.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.velocitypowered.api.proxy.Player;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class VelocitySkinsUser extends AbstractSkinsUser {

  private final Player parent;

  public VelocitySkinsUser(Player parent, SkinsPlugin plugin) {
    super(plugin);
    this.parent = parent;
  }

  @Override
  public void setNPCSkin(@NotNull Skin skin) {
    // do nothing - proxies can't have dynamic skin set
  }

  @Override
  public boolean openSkinMenu() {
    if (parent.getCurrentServer().isPresent()
        && HelloHandler.hasReceivedHello(
            parent.getCurrentServer().get().getServerInfo().getName())) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("OpenMenu");
      out.writeUTF(parent.getUniqueId().toString());
      parent
          .getCurrentServer()
          .get()
          .sendPluginMessage(VelocityPlugin.SKINS_PLUGIN_CHANNEL, out.toByteArray());
      return true;
    } else {
      sendMessage(
          TextComponent.of(
                  "Warn: proxy didn't receive hello from the server you're connected to; please report to server administrators")
              .color(NamedTextColor.YELLOW)
              .append(TextComponent.of("\n"))
              .append(
                  TextComponent.of(
                          "Error: 500 communicator not installed (report to server administrators)")
                      .color(NamedTextColor.RED)));
      return false;
    }
  }

  @Override
  public @NotNull String getName() {
    return parent.getUsername();
  }

  @Override
  public void sendMessage(Component message) {
    parent.sendMessage(message);
  }

  @Override
  public boolean hasPermission(String permission) {
    return parent.hasPermission(permission);
  }

  @Override
  public @NotNull UUID getUniqueId() {
    return parent.getUniqueId();
  }

  @Override
  public boolean isOnline() {
    return true;
  }
}
