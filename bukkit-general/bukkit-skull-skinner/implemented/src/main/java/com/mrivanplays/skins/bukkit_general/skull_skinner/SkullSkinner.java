package com.mrivanplays.skins.bukkit_general.skull_skinner;

import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface SkullSkinner {

  NamespacedKey SKULL_OWNER_NAME_KEY = new NamespacedKey("skins", "skull_owner_name");
  NamespacedKey SKULL_OWNER_UUID_KEY = new NamespacedKey("skins", "skull_owner_uuid");

  ItemStack buildItem(ItemSkin skin, String ownerName, String headNameFormat, List<String> lore);

  SkullOwner getSkullOwner(ItemStack item);
}
