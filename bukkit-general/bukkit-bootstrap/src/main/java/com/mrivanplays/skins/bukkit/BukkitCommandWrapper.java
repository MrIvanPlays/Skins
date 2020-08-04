package com.mrivanplays.skins.bukkit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrivanplays.commandworker.bukkit.BukkitCommand;
import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.commandworker.core.LiteralNode;
import com.mrivanplays.commandworker.core.argument.parser.ArgumentHolder;
import com.mrivanplays.skins.core.command.CommandSource;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitCommandWrapper implements BukkitCommand {

  private final Command<CommandSource> wrapped;
  private final BukkitCommandSourceManager sourceManager;

  public BukkitCommandWrapper(Command<CommandSource> wrapped, BukkitCommandSourceManager sourceManager) {
    this.wrapped = wrapped;
    this.sourceManager = sourceManager;
  }

  @Override
  public boolean execute(
      @NotNull CommandSender sender, @NotNull String label, @NotNull ArgumentHolder args)
      throws CommandSyntaxException {
    return wrapped.execute(sourceManager.obtain(sender), label, args);
  }

  @Override
  public @NotNull LiteralNode createCommandStructure() {
    return wrapped.createCommandStructure();
  }
}
