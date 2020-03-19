package com.mrivanplays.skins.bukkit.abstraction;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mrivanplays.skins.api.Skin;
import java.lang.reflect.Field;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_15_R1.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;

public class SkinSetter1_15_R1 implements SkinSetter {

  @Override
  public void setSkin(Player player, Skin skin) {
    ((CraftPlayer) player)
        .getProfile()
        .getProperties()
        .put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
  }

  @Override
  public ItemStack getMenuItem(
      ItemStack item, Skin skin, String ownerName, String headNameFormat, List<String> lore) {
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
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    meta.setDisplayName(headNameFormat.replace("%name%", ownerName));
    meta.setOwningPlayer(player);
    meta.setLore(lore);
    ItemStack duplicate = item.clone();
    duplicate.setItemMeta(meta);
    return duplicate;
  }
}