package com.mrivanplays.skins.bukkit_general;

import com.mrivanplays.pagedinventory.api.NavigationItem;
import com.mrivanplays.pagedinventory.api.PageClick;
import com.mrivanplays.pagedinventory.api.PagedInventory;
import com.mrivanplays.pagedinventory.api.PagedInventoryBuilder;
import com.mrivanplays.skins.bukkit_general.skull_skinner.ItemSkin;
import com.mrivanplays.skins.bukkit_general.skull_skinner.SkullOwner;
import com.mrivanplays.skins.bukkit_general.skull_skinner.SkullSkinner;
import com.mrivanplays.skins.bukkit_general.skull_skinner.SkullSkinnerHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class SkinsMenu {

  private final SkinsMenuAdapter menuAdapter;
  private final Plugin base;

  private final Consumer<PageClick> clickListener;
  private final ItemStack previousItem;
  private final ItemStack nextItem;
  private final ItemStack closeItem;

  public SkinsMenu(SkinsMenuAdapter menuAdapter, Plugin base) {
    this(
        menuAdapter,
        base,
        click -> {
          Player player = click.getClicker();
          ItemStack item = click.getClickedItem();
          if (item == null) {
            return;
          }
          SkullOwner owner = SkullSkinnerHandler.getSkullSkinner().getSkullOwner(item);
          ItemSkin skin = menuAdapter.getSkin(owner.getOwnerUUID());
          menuAdapter.dispatchSkinSet(player, skin, owner.getOwnerName());
          player.closeInventory();
        });
  }

  public SkinsMenu(SkinsMenuAdapter menuAdapter, Plugin base, Consumer<PageClick> clickListener) {
    this.menuAdapter = menuAdapter;
    this.base = base;

    previousItem =
        createItem(color(menuAdapter.getConfig().getSkinMenuPreviousPageLabel()), Material.PAPER);

    nextItem = createItem(color(menuAdapter.getConfig().getSkinMenuNextPageLabel()), Material.BOOK);

    closeItem =
        createItem(color(menuAdapter.getConfig().getSkinMenuClosePageLabel()), Material.BARRIER);

    this.clickListener = clickListener;
  }

  public void openMenu(Player player) {
    Map<Integer, ItemStack> itemCache = new HashMap<>();
    SkullSkinner itemSetter = SkullSkinnerHandler.getSkullSkinner();
    List<ItemSkin> skins = menuAdapter.getStoredSkins();
    for (int i = 0, len = skins.size(); i < len; i++) {
      ItemSkin skin = skins.get(i);
      ItemStack item =
          itemSetter.buildItem(
              skin,
              menuAdapter.getOwnerName(skin),
              color(menuAdapter.getConfig().getSkinMenuHeadName()),
              colorList(menuAdapter.getConfig().getSkinMenuLore()));
      itemCache.put(i, item);
    }

    List<Inventory> pages = getPages(getPagesRaw(itemCache));
    PagedInventoryBuilder pagedInventoryBuilder = PagedInventoryBuilder.createBuilder(base);
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
          color(
              menuAdapter
                  .getConfig()
                  .getSkinMenuInventory()
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
    return list.stream().map(SkinsMenu::color).collect(Collectors.toList());
  }

  private ItemStack createItem(String name, Material type) {
    ItemStack item = new ItemStack(type);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(name);
    item.setItemMeta(meta);
    return item;
  }

  private static String color(String message) {
    TextComponent comp = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    MiniMessage miniMessage = MiniMessage.markdown();
    return miniMessage.serialize(
        miniMessage.deserialize(LegacyComponentSerializer.legacySection().serialize(comp)));
  }
}
