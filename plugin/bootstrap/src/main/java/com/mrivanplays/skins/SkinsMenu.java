package com.mrivanplays.skins;

import com.mrivanplays.pagedinventory.api.NavigationItem;
import com.mrivanplays.pagedinventory.api.PagedInventory;
import com.mrivanplays.pagedinventory.api.PagedInventoryBuilder;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.handle.SkinSetterHandler;
import com.mrivanplays.skins.core.StoredSkin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SkinsMenu {

  private PagedInventory pagedInventory;

  public SkinsMenu(SkinsBukkitPlugin plugin) {
    Map<Integer, ItemStack> itemCache = new HashMap<>();
    List<StoredSkin> skins = plugin.getSkinStorage().deserialize();
    SkinSetter itemSetter = SkinSetterHandler.getSkinSetter();
    for (int i = 0; i < skins.size(); i++) {
      StoredSkin storedSkin = skins.get(i);
      ItemStack item =
          itemSetter.getMenuItem(
              new ItemStack(Material.PLAYER_HEAD), storedSkin.getSkin(), storedSkin.getName());
      itemCache.put(i, item);
    }

    //
    List<Inventory> pages = getPages(getPagesRaw(itemCache));
    PagedInventoryBuilder pagedInventoryBuilder = PagedInventoryBuilder.createBuilder(plugin);
    for (int i = 0; i < pages.size(); i++) {
      pagedInventoryBuilder.page(i + 1, pages.get(i));
    }
    pagedInventory =
        pagedInventoryBuilder
            .navigationItem(
                0,
                NavigationItem.create(
                    SkinsMenuSwitchItems.previousItem, NavigationItem.Action.PREVIOUS_PAGE))
            .navigationItem(
                4,
                NavigationItem.create(SkinsMenuSwitchItems.closeItem, NavigationItem.Action.CLOSE))
            .navigationItem(
                8,
                NavigationItem.create(
                    SkinsMenuSwitchItems.nextItem, NavigationItem.Action.NEXT_PAGE))
            .build();

    pagedInventory.addOnClickFunction(
        click -> {
          Player player = click.getClicker();
          ItemStack item = click.getClickedItem();
          if (item == null) {
            return;
          }
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
    pagedInventory.open(player, 1);
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
      Inventory inventory =
          Bukkit.createInventory(null, 54, "List of skins (Page #" + pageNum + ")");
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
