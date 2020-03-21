package com.mrivanplays.skins.core;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.SkullItemBuilder;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkullItemBuilderImpl implements SkullItemBuilder {

  private final Function<SkullItemBuilderData, ItemStack> transformer;
  private MojangResponse owner;
  private List<String> itemLore = Collections.emptyList();
  private String itemName;

  public SkullItemBuilderImpl(Function<SkullItemBuilderData, ItemStack> transformer) {
    this.transformer = transformer;
  }

  @Override
  public @NotNull SkullItemBuilder setOwner(@NotNull MojangResponse owner) {
    this.owner = owner;
    return this;
  }

  @Override
  public @NotNull SkullItemBuilder setItemName(@Nullable String name) {
    this.itemName = name;
    return this;
  }

  @Override
  public @NotNull SkullItemBuilder setItemLore(@Nullable List<String> lore) {
    if (lore == null) {
      return this;
    }
    this.itemLore = lore;
    return this;
  }

  @Override
  public @NotNull ItemStack build() {
    return transformer.apply(new SkullItemBuilderData(itemName, itemLore, owner));
  }

  public static final class SkullItemBuilderData {

    private final String itemName;
    private final List<String> lore;
    private final MojangResponse owner;

    public SkullItemBuilderData(String itemName, List<String> lore, MojangResponse owner) {
      this.itemName = itemName;
      this.lore = lore;
      this.owner = owner;
    }

    public String getItemName() {
      return itemName;
    }

    public List<String> getItemLore() {
      return lore;
    }

    public MojangResponse getOwner() {
      return owner;
    }
  }
}
