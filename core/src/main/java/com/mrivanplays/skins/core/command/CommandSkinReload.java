package com.mrivanplays.skins.core.command;

import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.commandworker.core.LiteralNode;
import com.mrivanplays.commandworker.core.argument.parser.ArgumentHolder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class CommandSkinReload implements Command<CommandSource> {

  @Override
  public boolean execute(
      @NotNull CommandSource source, @NotNull String label, @NotNull ArgumentHolder args) {
    source.sendMessage(
        TextComponent.of(
                "This command has been removed in v2.0.0 . The reason is that dynamic reloads may cause various of types of issues.")
            .color(NamedTextColor.RED));
    return true;
  }

  @Override
  public @NotNull LiteralNode createCommandStructure() {
    return LiteralNode.node().markShouldExecuteCommand();
  }
}
