package com.mrivanplays.skins.bukkit.abstraction;

import com.mrivanplays.skins.api.Skin;
import java.util.List;
import java.util.UUID;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SkinSetter {

  NamespacedKey SKULL_OWNER_NAME_KEY = new NamespacedKey("skins", "skull_owner_name");
  NamespacedKey SKULL_OWNER_UUID_KEY = new NamespacedKey("skins", "skull_owner_uuid");

  void setSkin(Player player, Skin skin);

  ItemStack getMenuItem(Skin skin, String ownerName, String headNameFormat, List<String> lore);

  SkullItemOwnerResponse getSkullOwner(ItemStack item);

  public static class SkullItemOwnerResponse {
    public String name;
    public UUID uuid;

    public SkullItemOwnerResponse(String name, UUID uuid) {
      this.name = name;
      this.uuid = uuid;
    }
  }
}
