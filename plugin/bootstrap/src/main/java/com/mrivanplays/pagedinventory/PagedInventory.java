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
package com.mrivanplays.pagedinventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PagedInventory implements Listener {

  private List<Inventory> inventories;
  private Map<UUID, Integer> currentPage = new HashMap<>();
  private Set<Consumer<ClickInfo>> clickHandlers;

  public PagedInventory(Plugin plugin, List<Inventory> inventories) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    this.inventories = inventories;
    this.clickHandlers = new HashSet<>();
  }

  public void openDefault(Player player) {
    player.openInventory(inventories.get(0));
    if (!currentPage.containsKey(player.getUniqueId())) {
      currentPage.put(player.getUniqueId(), 0);
    } else {
      currentPage.replace(player.getUniqueId(), 0);
    }
  }

  public void openPrevious(Player player) {
    int current = currentPage.get(player.getUniqueId());
    if ((inventories.size() - 1) == current) {
      openDefault(player);
      return;
    }
    int toOpen = current - 1;
    player.openInventory(inventories.get(toOpen));
    currentPage.replace(player.getUniqueId(), toOpen);
  }

  public void openNext(Player player) {
    int current = currentPage.get(player.getUniqueId());
    if ((inventories.size() + 1) > current) {
      return;
    }
    int toOpen = current + 1;
    player.openInventory(inventories.get(toOpen));
    currentPage.replace(player.getUniqueId(), toOpen);
  }

  public void addClickHandler(Consumer<ClickInfo> clickHandler) {
    clickHandlers.add(clickHandler);
  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    if (event.getClickedInventory() == null) {
      return;
    }
    if (event.getCurrentItem() == null) {
      return;
    }
    if (inventories.contains(event.getClickedInventory())) {
      event.setCancelled(true);
      if (event.getClick() != ClickType.LEFT) {
        return;
      }
      if (event.getSlot() == 0) {
        // previous page
        openPrevious(player);
        return;
      }
      if (event.getSlot() == 4) {
        // close inventory
        player.closeInventory();
        return;
      }
      if (event.getSlot() == 8) {
        // next page
        openNext(player);
        return;
      }

      ClickInfo clickInfo = new ClickInfo(player, event.getClickedInventory(), event.getCurrentItem());

      for (Consumer<ClickInfo> clickListener : clickHandlers) {
        clickListener.accept(clickInfo);
      }
    }

  }

  public static class ClickInfo {

    private Player player;
    private Inventory page;
    private ItemStack item;

    public ClickInfo(Player player, Inventory page, ItemStack item) {
      this.player = player;
      this.page = page;
      this.item = item;
    }

    public Player getPlayer() {
      return player;
    }

    public Inventory getPage() {
      return page;
    }

    public ItemStack getItem() {
      return item;
    }
  }
}
