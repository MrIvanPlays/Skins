package com.mrivanplays.skins.bukkit.abstraction;

import com.mrivanplays.skins.api.Skin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SkinSetter {

  void setSkin(Player player, Skin skin);

  ItemStack getMenuItem(ItemStack item, Skin skin, String ownerName);
}
