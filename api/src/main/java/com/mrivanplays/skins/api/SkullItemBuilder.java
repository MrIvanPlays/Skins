package com.mrivanplays.skins.api;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a skull item builder. This API was added in order for plugins using skulls to make use
 * of the caching Skins plugin is doing in order to prevent bukkit api from sending requests to
 * mojang api for skull owner skin.
 */
public interface SkullItemBuilder {

  /**
   * Sets the owner of the skull
   *
   * @param owner mojang api response for skin, representing the owner of the skull.
   * @return this instance for chaining
   */
  @NotNull
  SkullItemBuilder setOwner(@NotNull MojangResponse owner);

  /**
   * Sets the item's name. Placeholder "%name%" is available for owner name.
   *
   * @param name name of the item
   * @return this instance for chaining
   */
  @NotNull
  SkullItemBuilder setItemName(@Nullable String name);

  /**
   * Sets the item's lore.
   *
   * @param lore lore of the item
   * @return this instance for chaining
   */
  @NotNull
  SkullItemBuilder setItemLore(@Nullable List<String> lore);

  /**
   * Builds the set data into an {@link ItemStack}
   *
   * @return item
   */
  @NotNull
  ItemStack build();
}
