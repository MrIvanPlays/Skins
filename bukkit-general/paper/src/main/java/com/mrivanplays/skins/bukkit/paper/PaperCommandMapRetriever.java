package com.mrivanplays.skins.bukkit.paper;

import com.mrivanplays.skins.bukkit.core.CommandMapRetriever;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

public class PaperCommandMapRetriever extends CommandMapRetriever {

  @Override
  public CommandMap retrieveCommandMap() {
    return Bukkit.getCommandMap();
  }
}
