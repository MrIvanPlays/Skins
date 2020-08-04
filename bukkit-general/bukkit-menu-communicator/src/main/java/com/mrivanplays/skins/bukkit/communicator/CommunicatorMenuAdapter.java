package com.mrivanplays.skins.bukkit.communicator;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mrivanplays.skins.bukkit_general.SkinsConfigAdapter;
import com.mrivanplays.skins.bukkit_general.SkinsMenuAdapter;
import com.mrivanplays.skins.bukkit_general.skull_skinner.ItemSkin;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommunicatorMenuAdapter implements SkinsMenuAdapter {

  private final SkinsConfigAdapter configAdapter;
  private List<ItemSkin> storedSkins;
  private final SkinsBukkitCommunicator plugin;

  public CommunicatorMenuAdapter(SkinsBukkitCommunicator plugin) {
    this.configAdapter = plugin.getCachedConfigAdapter();
    storedSkins = plugin.getStoredSkins();
    this.plugin = plugin;
  }

  @Override
  public ItemSkin getSkin(UUID uuid) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("GetSkin");
    out.writeUTF(uuid.toString());
    Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
    player.sendPluginMessage(plugin, "skins:plugin", out.toByteArray());
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return plugin.getSkins().getOrDefault(uuid, null);
  }

  @Override
  public String getOwnerName(ItemSkin skin) {
    return ((CommunicatorItemSkin) skin).getOwnerName();
  }

  @Override
  public void dispatchSkinSet(Player player, ItemSkin skin, String skinOwnerName) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    if (skin != null) {
      out.writeUTF("SetSkin");
      out.writeUTF(player.getUniqueId().toString());
      out.writeUTF(skin.getOwner().toString());
      out.writeUTF(skinOwnerName);
    } else {
      out.writeUTF("SkinSetFailure");
      out.writeUTF(player.getUniqueId().toString());
    }
    player.sendPluginMessage(plugin, "skins:plugin", out.toByteArray());
  }

  @Override
  public List<ItemSkin> getStoredSkins() {
    return storedSkins;
  }

  @Override
  public SkinsConfigAdapter getConfig() {
    return configAdapter;
  }
}
