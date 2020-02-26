package com.mrivanplays.skins;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SkinsMenuSwitchItems {

  public static ItemStack previousItem;
  public static ItemStack nextItem;
  public static ItemStack closeItem;

  static {
    previousItem = new ItemCreateUtil(Material.PAPER).name("Previous page").create();
    nextItem = new ItemCreateUtil(Material.BOOK).name("Next page").create();
    closeItem = new ItemCreateUtil(Material.BARRIER).name("Close inventory").create();
  }
}
