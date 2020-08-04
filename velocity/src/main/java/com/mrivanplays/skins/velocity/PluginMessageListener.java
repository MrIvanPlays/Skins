package com.mrivanplays.skins.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsUser;
import com.mrivanplays.skins.core.storage.StoredSkin;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PluginMessageListener implements EventHandler<PluginMessageEvent> {

  public static void register(VelocityPlugin bootstrap, VelocitySkinsPlugin plugin) {
    PluginMessageListener listener = new PluginMessageListener(plugin, bootstrap.getProxy());
    bootstrap.getProxy().getEventManager().register(bootstrap, PluginMessageEvent.class, listener);
  }

  private final VelocitySkinsPlugin plugin;
  private final ProxyServer proxy;

  public PluginMessageListener(VelocitySkinsPlugin plugin, ProxyServer proxy) {
    this.plugin = plugin;
    this.proxy = proxy;
  }

  @Override
  public void execute(PluginMessageEvent event) {
    ChannelIdentifier identifier = event.getIdentifier();
    if (!identifier.getId().equalsIgnoreCase("skins:plugin")) {
      return;
    }
    ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
    String subchannel = in.readUTF();
    if (subchannel.equalsIgnoreCase("Hello")) {
      UUID uuid = UUID.fromString(in.readUTF());
      proxy
          .getPlayer(uuid)
          .flatMap(Player::getCurrentServer)
          .ifPresent(
              serverConn -> {
                if (HelloHandler.hasReceivedHello(serverConn.getServerInfo().getName())) {
                  return;
                }
                HelloHandler.addHelloReceived(serverConn.getServerInfo().getName());
              });
    }
    ServerConnection source = (ServerConnection) event.getSource();
    if (subchannel.equalsIgnoreCase("GetSkin")) {
      UUID uuid = UUID.fromString(in.readUTF());
      Skin skin = plugin.getApiImpl().getSkinAccessor().getSkin(uuid, true).join();
      StoredSkin storedSkin = plugin.getStorage().find(skin.getOwner()).join();

      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("GetSkin");
      out.writeUTF(storedSkin.getOwnerName());
      out.writeUTF(skin.getOwner().toString());
      out.writeUTF(skin.getTexture());
      out.writeUTF(skin.getSignature());
      source.sendPluginMessage(VelocityPlugin.SKINS_PLUGIN_CHANNEL, out.toByteArray());
    }
    if (subchannel.equalsIgnoreCase("GetAllSkins")) {
      List<StoredSkin> storedSkins = plugin.getStorage().all().join();
      if (storedSkins.isEmpty()) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("LastGetAllSkins");
        out.writeUTF("empty");
        source.sendPluginMessage(VelocityPlugin.SKINS_PLUGIN_CHANNEL, out.toByteArray());
        return;
      }
      for (int i = 0, len = storedSkins.size(); i < len; i++) {
        StoredSkin storedSkin = storedSkins.get(i);
        Skin skin = storedSkin.getSkin();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        String subchannelName = "GetAllSkins";
        if (i == (len - 1)) {
          subchannelName = "LastGetAllSkins";
        }
        out.writeUTF(subchannelName);
        out.writeUTF(storedSkin.getOwnerName());
        out.writeUTF(skin.getOwner().toString());
        out.writeUTF(skin.getTexture());
        out.writeUTF(skin.getSignature());
        source.sendPluginMessage(VelocityPlugin.SKINS_PLUGIN_CHANNEL, out.toByteArray());
      }
    }
    if (subchannel.equalsIgnoreCase("SetSkin")) {
      Optional<Player> playerOpt = proxy.getPlayer(UUID.fromString(in.readUTF()));
      if (playerOpt.isPresent()) {
        UUID skinOwner = UUID.fromString(in.readUTF());
        String skinOwnerName = in.readUTF();
        AbstractSkinsUser user =
            (AbstractSkinsUser) plugin.obtainUser(playerOpt.get().getUniqueId());
        if (user == null) {
          // replicate the hack in
          // com.mrivanplays.skins.core.command.CommandSource#sendMessage(String)
          TextComponent comp =
              LegacyComponentSerializer.legacy('&')
                  .deserialize(
                      plugin.getConfiguration().getMessages().getSkinMenuCannotFetchData());
          MiniMessage miniMessage = MiniMessage.markdown();
          playerOpt.get().sendMessage(miniMessage.deserialize(miniMessage.serialize(comp)));
          return;
        }
        Skin skin = plugin.getApiImpl().getSkinAccessor().getSkin(skinOwner, true).join();
        plugin.dispatchSkinSet(user, Optional.ofNullable(skin), skinOwnerName);
      }
    }
    if (subchannel.equalsIgnoreCase("SkinSetFailure")) {
      Optional<Player> playerOpt = proxy.getPlayer(UUID.fromString(in.readUTF()));
      if (playerOpt.isPresent()) {
        SkinsUser user = plugin.obtainUser(playerOpt.get().getUniqueId());
        if (user == null) {
          return;
        }
        user.sendMessage(plugin.getConfiguration().getMessages().getNotPremium());
      }
    }
  }
}
