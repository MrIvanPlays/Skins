package com.mrivanplays.skins.bukkit_general.skull_skinner;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mrivanplays.skins.api.Skin;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullSkinner1_12_R1 implements SkullSkinner {

  @Override
  public ItemStack buildItem(
      Skin skin, String ownerName, String headNameFormat, List<String> lore) {
    ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
    if (skin != null) {
      net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
      NBTTagCompound tag = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
      NBTTagCompound skins = new NBTTagCompound();
      NBTTagCompound owner = new NBTTagCompound();
      owner.set("name", new NBTTagString(ownerName));
      owner.set("uuid", new NBTTagString(skin.getOwner().toString()));
      skins.set("skullOwner", owner);
      tag.set("skins", skins);
      nms.setTag(tag);
      item = CraftItemStack.asBukkitCopy(nms);
    }
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    if (skin != null) {
      GameProfile profile = new GameProfile(skin.getOwner(), ownerName);
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
    if (item.getType() != Material.SKULL_ITEM) {
      return null;
    }
    if (!item.hasItemMeta()) {
      return null;
    }
    if (!(item.getItemMeta() instanceof SkullMeta)) {
      return null;
    }
    net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
    NBTTagCompound tag = nms.getTag();
    if (tag == null) {
      return null;
    }
    NBTTagCompound skins = tag.getCompound("skins");
    NBTTagCompound owner = skins.getCompound("skullOwner");
    String ownerName = owner.getString("name");
    UUID ownerUUID =
        owner.getString("uuid").isEmpty() ? null : UUID.fromString(owner.getString("uuid"));
    if (ownerName.isEmpty() || ownerUUID == null) {
      return null;
    }
    return new SkullOwner(ownerName, ownerUUID);
  }
}
