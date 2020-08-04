package com.mrivanplays.skins.core.command;

import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.commandworker.core.LiteralNode;
import com.mrivanplays.commandworker.core.argument.parser.ArgumentHolder;
import com.mrivanplays.skins.core.SkinsPlugin;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class CommandSkinInfo implements Command<CommandSource> {

  private final SkinsPlugin plugin;

  public CommandSkinInfo(SkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean execute(
      @NotNull CommandSource source, @NotNull String label, @NotNull ArgumentHolder args) {
    String version = plugin.getInfo().getVersion();
    // yes I'm recreating it cuz I don't want to have a method just for that
    String implementationVersion =
        "git:Skins:"
            + version
            + ":"
            + plugin.getInfo().getCommit()
            + ":"
            + (plugin.getInfo().getBuildNumber() == -1
                ? "unknown"
                : plugin.getInfo().getBuildNumber());
    source.sendMessage(TextComponent.of("Skins v" + version + " by MrIvanPlays"));
    source.sendMessage(TextComponent.of("Exact version: " + implementationVersion));
    if (version.contains("SNAPSHOT")) {
      source.sendMessage(
          TextComponent.of(
                  "THIS IS A DEVELOPMENT BUILD! EVERY BUG SHOULD BE REPORTED TO THE ISSUE TRACKER !!!!")
              .color(NamedTextColor.RED));
    } else {
      source.sendMessage(
          TextComponent.of("You are running a version which is considered stable.")
              .color(NamedTextColor.GREEN));
    }
    return true;
  }

  @Override
  public @NotNull LiteralNode createCommandStructure() {
    return LiteralNode.node().markShouldExecuteCommand();
  }
}
