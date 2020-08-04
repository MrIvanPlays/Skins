package com.mrivanplays.skins.bukkit.communicator;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mrivanplays.skins.bukkit_general.SkinsMenu;
import com.mrivanplays.skins.bukkit_general.skull_skinner.ItemSkin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class SkinsBukkitCommunicator extends JavaPlugin implements PluginMessageListener {

  private CommunicatorConfigAdapter cachedConfigAdapter;

  private Map<UUID, ItemSkin> skins = new HashMap<>();
  private List<ItemSkin> storedSkins = new ArrayList<>();
  private List<UUID> skinMenuOpenRequests = new ArrayList<>();

  @Override
  public void onEnable() {
    getServer().getMessenger().registerIncomingPluginChannel(this, "skins:plugin", this);
    getServer().getMessenger().registerOutgoingPluginChannel(this, "skins:plugin");
    cachedConfigAdapter = new CommunicatorConfigAdapter(getDataFolder());
    getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
  }

  @Override
  public void onPluginMessageReceived(
      @NotNull String channel, @NotNull Player player, @NotNull byte[] data) {
    if (!channel.equalsIgnoreCase("skins:plugin")) {
      return;
    }
    ByteArrayDataInput in = ByteStreams.newDataInput(data);
    String subchannel = in.readUTF();
    if (subchannel.equalsIgnoreCase("GetSkin")) {
      String name = in.readUTF();
      UUID owner = UUID.fromString(in.readUTF());
      String texture = in.readUTF();
      String signature = in.readUTF();
      skins.put(owner, new CommunicatorItemSkin(owner, texture, signature, name));
    }
    if (subchannel.equalsIgnoreCase("GetAllSkins")) {
      String name = in.readUTF();
      UUID owner = UUID.fromString(in.readUTF());
      String texture = in.readUTF();
      String signature = in.readUTF();
      storedSkins.add(new CommunicatorItemSkin(owner, texture, signature, name));
    }
    if (subchannel.equalsIgnoreCase("LastGetAllSkins")) {
      String name = in.readUTF();
      if (name.equalsIgnoreCase("empty")) {
        handleMenu();
        return;
      }
      UUID owner = UUID.fromString(in.readUTF());
      String texture = in.readUTF();
      String signature = in.readUTF();
      storedSkins.add(new CommunicatorItemSkin(owner, texture, signature, name));
      handleMenu();
    }
    if (subchannel.equalsIgnoreCase("OpenMenu")) {
      skinMenuOpenRequests.add(UUID.fromString(in.readUTF()));
      if (storedSkins.isEmpty()) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetAllSkins");
        player.sendPluginMessage(this, "skins:plugin", out.toByteArray());
      }
    }
  }

  private void handleMenu() {
    SkinsMenu menu = new SkinsMenu(new CommunicatorMenuAdapter(this), this);
    for (UUID requests : skinMenuOpenRequests) {
      Player open = getServer().getPlayer(requests);
      if (open == null) {
        continue;
      }
      menu.openMenu(open);
    }
    getServer()
        .getScheduler()
        .scheduleSyncDelayedTask(
            this,
            () -> {
              skinMenuOpenRequests.clear();
              storedSkins.clear();
              skins.clear();
            },
            20);
  }

  public CommunicatorConfigAdapter getCachedConfigAdapter() {
    return cachedConfigAdapter;
  }

  public Map<UUID, ItemSkin> getSkins() {
    return skins;
  }

  public List<ItemSkin> getStoredSkins() {
    return storedSkins;
  }
}
