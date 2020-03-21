package com.mrivanplays.skins.bukkit.abstraction;

import com.mrivanplays.skins.api.Skin;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SkinSetter {

  void setSkin(Player player, Skin skin);

  ItemStack getMenuItem(Skin skin, String ownerName, String headNameFormat, List<String> lore);
}
