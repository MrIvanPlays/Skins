package com.mrivanplays.skins;

import com.mrivanplays.pagedinventory.api.NavigationItem;
import com.mrivanplays.pagedinventory.api.PageClick;
import com.mrivanplays.pagedinventory.api.PagedInventory;
import com.mrivanplays.pagedinventory.api.PagedInventoryBuilder;
import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.handle.SkinSetterHandler;
import com.mrivanplays.skins.core.StoredSkin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SkinsMenu {

  private SkinsBukkitPlugin plugin;

  private final Consumer<PageClick> clickListener;
  private final ItemStack previousItem;
  private final ItemStack nextItem;
  private final ItemStack closeItem;

  public SkinsMenu(SkinsBukkitPlugin plugin) {
    this.plugin = plugin;

    previousItem =
        createItem(
            plugin.color(plugin.getConfig().getString("messages.skin-menu-previous-page-label")),
            Material.PAPER);

    nextItem =
        createItem(
            plugin.color(plugin.getConfig().getString("messages.skin-menu-next-page-label")),
            Material.BOOK);

    closeItem =
        createItem(
            plugin.color(plugin.getConfig().getString("messages.skin-menu-close-page-label")),
            Material.BARRIER);

    clickListener =
        click -> {
          Player player = click.getClicker();
          ItemStack item = click.getClickedItem();
          if (item == null) {
            return;
          }
          MojangResponse response = plugin.getApi().getSkullOwner(item);
          if (response == null) {
            player.sendMessage(
                plugin.color(plugin.getConfig().getString("messages.skin-menu-cannot-fetch-data")));
            player.closeInventory();
            return;
          }

          plugin.getServer().dispatchCommand(player, "skinset " + response.getNickname());
          player.closeInventory();
        };
  }

  public void openMenu(Player player) {
    Map<Integer, ItemStack> itemCache = new HashMap<>();
    List<StoredSkin> skins = plugin.getSkinStorage().deserialize();
    SkinSetter itemSetter = SkinSetterHandler.getSkinSetter();
    for (int i = 0; i < skins.size(); i++) {
      StoredSkin storedSkin = skins.get(i);
      ItemStack item =
          itemSetter.getMenuItem(
              storedSkin.getSkin(),
              storedSkin.getName(),
              plugin.color(plugin.getConfig().getString("messages.skin-menu-head-name")),
              colorList(plugin.getConfig().getStringList("messages.skin-menu-lore")));
      itemCache.put(i, item);
    }

    List<Inventory> pages = getPages(getPagesRaw(itemCache));
    PagedInventoryBuilder pagedInventoryBuilder = PagedInventoryBuilder.createBuilder(plugin);
    for (int i = 0; i < pages.size(); i++) {
      pagedInventoryBuilder.page(i + 1, pages.get(i));
    }

    PagedInventory pagedInventory =
        pagedInventoryBuilder
            .navigationItem(
                0, NavigationItem.create(previousItem, NavigationItem.Action.PREVIOUS_PAGE))
            .navigationItem(4, NavigationItem.create(closeItem, NavigationItem.Action.CLOSE))
            .navigationItem(8, NavigationItem.create(nextItem, NavigationItem.Action.NEXT_PAGE))
            .clickFunction(clickListener)
            .build();

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
      String title =
          plugin.color(
              plugin
                  .getConfig()
                  .getString("messages.skin-menu-inventory")
                  .replace("%pageNum%", Integer.toString(pageNum)));
      Inventory inventory = Bukkit.createInventory(null, 54, title);
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

  private List<String> colorList(List<String> list) {
    return list.stream()
        .map(s -> ChatColor.translateAlternateColorCodes('&', s))
        .collect(Collectors.toList());
  }

  private ItemStack createItem(String name, Material type) {
    ItemStack item = new ItemStack(type);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(name);
    item.setItemMeta(meta);
    return item;
  }
}
