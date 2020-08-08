package com.mrivanplays.skins.velocity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.commandworker.core.LiteralNode;
import com.mrivanplays.commandworker.core.argument.parser.ArgumentHolder;
import com.mrivanplays.commandworker.velocity.VelocityCommand;
import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;

public class VelocityCommandWrapper implements VelocityCommand {

  private final Command<com.mrivanplays.skins.core.command.CommandSource> wrapped;
  private final VelocityCommandSourceManager sourceManager;

  public VelocityCommandWrapper(
      Command<com.mrivanplays.skins.core.command.CommandSource> wrapped,
      VelocityCommandSourceManager sourceManager) {
    this.wrapped = wrapped;
    this.sourceManager = sourceManager;
  }

  @Override
  public boolean execute(
      @NotNull CommandSource sender, @NotNull String label, @NotNull ArgumentHolder args)
      throws CommandSyntaxException {
    return wrapped.execute(sourceManager.obtainSource(sender), label, args);
  }

  @Override
  public @NotNull LiteralNode createCommandStructure() {
    return wrapped.createCommandStructure();
  }
}
