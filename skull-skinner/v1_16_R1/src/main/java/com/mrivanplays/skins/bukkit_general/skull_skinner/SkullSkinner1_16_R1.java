package com.mrivanplays.skins.bukkit_general.skull_skinner;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mrivanplays.skins.api.Skin;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

public class SkullSkinner1_16_R1 implements SkullSkinner {

  @Override
  public ItemStack buildItem(
      Skin skin, String ownerName, UUID ownerUUID, String headNameFormat, List<String> lore) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    if (skin != null) {
      GameProfile profile = new GameProfile(ownerUUID, ownerName);
      profile
          .getProperties()
          .put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
      try {
        Field profileField = meta.getClass().getDeclaredField("profile");
        profileField.setAccessible(true);
        profileField.set(meta, profile);
      } catch (IllegalAccessException | NoSuchFieldException e) {
        e.printStackTrace();
      }
      meta.getPersistentDataContainer()
          .set(SKULL_OWNER_NAME_KEY, PersistentDataType.STRING, ownerName);
      meta.getPersistentDataContainer()
          .set(SKULL_OWNER_UUID_KEY, PersistentDataType.STRING, ownerUUID.toString());
    }
    if (headNameFormat != null) {
      meta.setDisplayName(headNameFormat.replace("%name%", ownerName));
    }
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  @Override
  public SkullOwner getSkullOwner(ItemStack item) {
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
        meta.getPersistentDataContainer().get(SKULL_OWNER_NAME_KEY, PersistentDataType.STRING);
    String ownerUUIDString =
        meta.getPersistentDataContainer().get(SKULL_OWNER_UUID_KEY, PersistentDataType.STRING);
    if (ownerName == null || ownerUUIDString == null) {
      return null;
    }
    return new SkullOwner(ownerName, UUID.fromString(ownerUUIDString));
  }
}
