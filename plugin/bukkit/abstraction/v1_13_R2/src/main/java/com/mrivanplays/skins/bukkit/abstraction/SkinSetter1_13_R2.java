package com.mrivanplays.skins.bukkit.abstraction;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mrivanplays.skins.api.Skin;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

public class SkinSetter1_13_R2 implements SkinSetter {

  @Override
  public void setSkin(Player player, Skin skin) {
    ((CraftPlayer) player)
        .getProfile()
        .getProperties()
        .put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
  }

  @Override
  public ItemStack getMenuItem(
      Skin skin, String ownerName, String headNameFormat, List<String> lore) {
    if (skin == null) {
      ItemStack item = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta) item.getItemMeta();
      if (headNameFormat != null) {
        meta.setDisplayName(headNameFormat.replace("%name%", ownerName));
      }
      meta.setLore(lore);
      item.setItemMeta(meta);
      return item;
    }
    CraftOfflinePlayer player = (CraftOfflinePlayer) Bukkit.getOfflinePlayer(skin.getOwner());
    GameProfile profile = new GameProfile(skin.getOwner(), ownerName);
    profile
        .getProperties()
        .put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
    try {
      Field profileField = player.getClass().getDeclaredField("profile");
      profileField.setAccessible(true);
      profileField.set(player, profile);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
    }
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    meta.getCustomTagContainer()
        .setCustomTag(SkinSetter.SKULL_OWNER_NAME_KEY, ItemTagType.STRING, ownerName);
    meta.getCustomTagContainer()
        .setCustomTag(
            SkinSetter.SKULL_OWNER_UUID_KEY, ItemTagType.STRING, skin.getOwner().toString());
    if (headNameFormat != null) {
      meta.setDisplayName(headNameFormat.replace("%name%", ownerName));
    }
    meta.setOwningPlayer(player);
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  @Override
  public SkullItemOwnerResponse getSkullOwner(ItemStack item) {
    if (item.getType() != Material.PLAYER_HEAD) {
      return null;
    }
    if (!item.hasItemMeta()) {
      return null;
    }
    if (!(item.getItemMeta() instanceof SkullMeta)) {
      return null;
    }
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    String ownerName =
        meta.getCustomTagContainer()
            .getCustomTag(SkinSetter.SKULL_OWNER_NAME_KEY, ItemTagType.STRING);
    String ownerUUIDString =
        meta.getCustomTagContainer()
            .getCustomTag(SkinSetter.SKULL_OWNER_UUID_KEY, ItemTagType.STRING);
    if (ownerName == null || ownerUUIDString == null) {
      return null;
    }
    return new SkullItemOwnerResponse(ownerName, UUID.fromString(ownerUUIDString));
  }
}
