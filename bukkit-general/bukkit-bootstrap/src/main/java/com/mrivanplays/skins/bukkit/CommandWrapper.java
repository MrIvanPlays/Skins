package com.mrivanplays.skins.bukkit;

import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.command.CommandSource;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandWrapper implements TabExecutor {

  private final CommandSourceManager sourceManager;
  private final com.mrivanplays.skins.core.command.Command command;
  private final SkinsPlugin plugin;

  public CommandWrapper(
      com.mrivanplays.skins.core.command.Command command,
      CommandSourceManager sourceManager,
      SkinsPlugin plugin) {
    this.command = command;
    this.sourceManager = sourceManager;
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    CommandSource source = sourceManager.obtain(sender);
    if (!this.command.hasPermission(source)) {
      source.sendMessage(plugin.getConfiguration().getMessages().getNoPermission());
      return true;
    }
    this.command.execute(source, args);
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String alias,
      @NotNull String[] args) {
    CommandSource source = sourceManager.obtain(sender);
    if (!this.command.hasPermission(source)) {
      return Collections.emptyList();
    }
    return this.command.complete(source, args);
  }
}
