package com.mrivanplays.skins;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemCreateUtil {

  private ItemStack currentItem;

  public ItemCreateUtil(Material material) {
    currentItem = new ItemStack(material);
  }

  public ItemCreateUtil name(String name) {
    ItemMeta meta = currentItem.getItemMeta();
    meta.setDisplayName(name);
    currentItem.setItemMeta(meta);
    return this;
  }

  public ItemStack create() {
    return currentItem;
  }

}
