package com.mrivanplays.skins.bukkit;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandRegistration extends Command {

  private final com.mrivanplays.skins.core.command.Command parent;
  private final CommandSourceManager sourceManager;

  public CommandRegistration(
      @NotNull String name,
      com.mrivanplays.skins.core.command.Command parent,
      CommandSourceManager sourceManager,
      String noPermissionMessage) {
    super(name);
    this.parent = parent;
    this.sourceManager = sourceManager;
    this.setPermissionMessage(noPermissionMessage);
  }

  @Override
  public boolean execute(
      @NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
    parent.execute(sourceManager.obtain(sender), args);
    return true;
  }

  @Override
  public @NotNull List<String> tabComplete(
      @NotNull CommandSender sender,
      @NotNull String alias,
      @NotNull String[] args,
      @Nullable Location location)
      throws IllegalArgumentException {
    return parent.complete(sourceManager.obtain(sender), args);
  }

  @Override
  public boolean testPermissionSilent(@NotNull CommandSender target) {
    return parent.hasPermission(sourceManager.obtain(target));
  }
}
