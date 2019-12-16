/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.skins;

import com.mrivanplays.pagedinventory.PagedInventory;
import com.mrivanplays.skins.core.StoredSkin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@SuppressWarnings("deprecation")
public class SkinsMenu {

  private PagedInventory pagedInventory;

  public SkinsMenu(SkinsBukkitPlugin plugin) {
    Map<Integer, ItemStack> itemCache = new HashMap<>();
    List<StoredSkin> skins = plugin.getSkinStorage().deserialize();
    for (int i = 0; i < skins.size(); i++) {
      StoredSkin storedSkin = skins.get(i);
      ItemStack item = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta) item.getItemMeta();
      meta.setOwner(storedSkin.getName());
      meta.setDisplayName(storedSkin.getName() + " skin");
      meta.setLore(Arrays.asList("Left click to set the skin",
          "(Keep in mind this skin is being cached and may not be up to date)"));
      item.setItemMeta(meta);
      itemCache.put(i, item);
    }

    //
    List<Inventory> pages = getPages(getPagesRaw(itemCache));
    pagedInventory = new PagedInventory(plugin, pages);
    pagedInventory.addClickHandler(clickInfo -> {
      Player player = clickInfo.getPlayer();
      ItemStack item = clickInfo.getItem();
      if (!item.hasItemMeta()) {
        return;
      }
      if (!item.getItemMeta().hasDisplayName()) {
        return;
      }
      String displayName = item.getItemMeta().getDisplayName();
      int space = displayName.indexOf(' ');
      String skinName = displayName.substring(0, space).trim();

      plugin.getServer().dispatchCommand(player, "skinset " + skinName);
      player.closeInventory();
    });
  }

  public void openMenu(Player player) {
    pagedInventory.openDefault(player);
  }

  private List<Map<Integer, ItemStack>> getPagesRaw(Map<Integer, ItemStack> items) {
    List<Map<Integer, ItemStack>> list = new ArrayList<>();
    if (items.size() < 45) {
      list.add(items);
    } else {
      Map<Integer, ItemStack> copy = copy(items, 45);
      if (copy.size() > 45) {
        list.addAll(getPagesRaw(copy));
      } else {
        list.add(copy);
      }
    }
    return list;
  }

  public List<Inventory> getPages(List<Map<Integer, ItemStack>> pagesRaw) {
    List<Inventory> pages = new ArrayList<>();
    int pageNum = 1;
    for (Map<Integer, ItemStack> page : pagesRaw) {
      Inventory inventory = Bukkit
          .createInventory(null, 54, "List of skins (Page #" + pageNum + ")");
      inventory.setItem(0, SkinsMenuSwitchItems.previousItem);
      inventory.setItem(4, SkinsMenuSwitchItems.closeItem);
      inventory.setItem(8, SkinsMenuSwitchItems.nextItem);
      for (Map.Entry<Integer, ItemStack> entry : page.entrySet()) {
        inventory.setItem(entry.getKey() + 9, entry.getValue());
      }
      pages.add(inventory);
      pageNum++;
    }
    return pages;
  }

  private Map<Integer, ItemStack> copy(Map<Integer, ItemStack> map, int startPosition) {
    Map<Integer, ItemStack> copy = new HashMap<>();
    int mapPos = 0;
    for (int i = startPosition + 1; i < map.size(); i++) {
      copy.put(mapPos, map.get(i));
      mapPos++;
    }
    return copy;
  }
}
