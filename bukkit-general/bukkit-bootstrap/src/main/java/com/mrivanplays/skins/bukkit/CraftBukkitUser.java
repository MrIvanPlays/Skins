package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.bukkit.core.GeneralBukkitUser;
import com.mrivanplays.skins.bukkit_general.SkinsMenu;
import com.mrivanplays.skins.core.SkinsPlugin;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CraftBukkitUser extends GeneralBukkitUser {

  public CraftBukkitUser(SkinsPlugin plugin, SkinsMenu menuInstance, OfflinePlayer player) {
    super(plugin, menuInstance, player);
  }

  @Override
  public void setNPCSkin(@NotNull Skin skin) {
    if (!isOnline()) {
      return;
    }
    // todo: fix for 1.16
    try {
      Player p = getOnlineVariant();
      Method getProfileMethod = p.getClass().getDeclaredMethod("getProfile");
      getProfileMethod.setAccessible(true);
      Object profile = getProfileMethod.invoke(p, null);
      Method getPropertiesMethod = profile.getClass().getDeclaredMethod("getProperties");
      getProfileMethod.setAccessible(true);
      Object properties = getPropertiesMethod.invoke(profile, null);
      Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");

      Method putMethod =
          properties.getClass().getSuperclass().getDeclaredMethod("put", Object.class, Object.class);
      putMethod.setAccessible(true);

      Constructor propertyConstructor =
          propertyClass.getDeclaredConstructor(String.class, String.class, String.class);
      propertyConstructor.setAccessible(true);
      putMethod.invoke(
          properties,
          "textures",
          propertyConstructor.newInstance("textures", skin.getTexture(), skin.getSignature()));
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException
        | ClassNotFoundException
        | InstantiationException e) {
      e.printStackTrace();
    }
  }
}
