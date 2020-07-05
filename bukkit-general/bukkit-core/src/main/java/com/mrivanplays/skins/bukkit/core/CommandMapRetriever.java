package com.mrivanplays.skins.bukkit.core;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

public class CommandMapRetriever {

  public CommandMap retrieveCommandMap() {
    try {
      Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
      field.setAccessible(true);
      return (CommandMap) field.get(Bukkit.getServer());
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
}
