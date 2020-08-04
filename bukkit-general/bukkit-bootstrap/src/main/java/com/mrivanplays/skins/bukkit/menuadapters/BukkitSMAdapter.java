package com.mrivanplays.skins.bukkit.menuadapters;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.bukkit.BukkitSkinsPlugin;
import com.mrivanplays.skins.bukkit_general.SkinsConfigAdapter;
import com.mrivanplays.skins.bukkit_general.SkinsMenuAdapter;
import com.mrivanplays.skins.bukkit_general.skull_skinner.ItemSkin;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeCordComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class BukkitSMAdapter implements SkinsMenuAdapter {

  private final BukkitSkinsPlugin plugin;
  private final SkinsConfigAdapter configAdapter;

  public BukkitSMAdapter(BukkitSkinsPlugin plugin) {
    this.plugin = plugin;
    this.configAdapter = new BukkitSMConfigA(plugin.getConfiguration());
  }

  @Override
  public ItemSkin getSkin(UUID uuid) {
    Skin skin = plugin.getApiImpl().getSkinAccessor().getSkin(uuid, true).join();
    return skin == null ? null : new BukkitItemSkin(skin);
  }

  @Override
  public String getOwnerName(ItemSkin skin) {
    return ((BukkitStoredItemSkin) skin).getOwnerName();
  }

  @Override
  public void dispatchSkinSet(Player player, ItemSkin skin, String skinOwnerName) {
    AbstractSkinsUser skinsUser = plugin.obtainUser(player);
    if (skinsUser == null) {
      // replicate the hack in com.mrivanplays.skins.core.command.CommandSource#sendMessage(String)
      TextComponent comp =
          LegacyComponentSerializer.legacy('&')
              .deserialize(configAdapter.getSkinMenuCannotFetchData());
      MiniMessage miniMessage = MiniMessage.markdown();
      player
          .spigot()
          .sendMessage(
              BungeeCordComponentSerializer.get()
                  .serialize(miniMessage.deserialize(miniMessage.serialize(comp))));
      return;
    }
    plugin.dispatchSkinSet(
        skinsUser, skin == null ? Optional.empty() : Optional.of(toSkin(skin)), skinOwnerName);
  }

  private Skin toSkin(ItemSkin skin) {
    return new Skin(skin.getOwner(), skin.getTexture(), skin.getSignature());
  }

  @Override
  public List<ItemSkin> getStoredSkins() {
    return new ArrayList<>(Utils.map(plugin.getStorage().all().join(), BukkitStoredItemSkin::new));
  }

  @Override
  public SkinsConfigAdapter getConfig() {
    return configAdapter;
  }
}
