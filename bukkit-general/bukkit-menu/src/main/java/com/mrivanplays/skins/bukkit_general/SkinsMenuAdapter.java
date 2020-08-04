package com.mrivanplays.skins.bukkit_general;

import com.mrivanplays.skins.bukkit_general.skull_skinner.ItemSkin;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface SkinsMenuAdapter {

  ItemSkin getSkin(UUID uuid);

  String getOwnerName(ItemSkin skin);

  void dispatchSkinSet(Player player, ItemSkin skin, String skinOwnerName);

  List<ItemSkin> getStoredSkins();

  SkinsConfigAdapter getConfig();
}
