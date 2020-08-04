package com.mrivanplays.skins.core.command;

import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.commandworker.core.LiteralNode;
import com.mrivanplays.commandworker.core.argument.parser.ArgumentHolder;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkinsUser;
import com.mrivanplays.skins.core.UserCooldownRegistry;
import org.jetbrains.annotations.NotNull;

public class CommandSkinMenu implements Command<CommandSource> {

  private final SkinsPlugin plugin;

  public CommandSkinMenu(SkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean execute(
      @NotNull CommandSource source, @NotNull String label, @NotNull ArgumentHolder args) {
    SkinsConfiguration.Messages messages = plugin.getConfiguration().getMessages();
    if (!source.isPlayer()) {
      source.sendMessage(messages.getNoConsole());
      return true;
    }
    SkinsUser user = plugin.obtainUser(source.getName());
    long cooldownRemaining = UserCooldownRegistry.MENU.getTimeLeft(user.getUniqueId());
    if (cooldownRemaining > 0) {
      user.sendMessage(
          messages.getCooldown().replace("%timeLeft%", Long.toString(cooldownRemaining)));
      return true;
    }
    if (user.openSkinMenu()) {
      UserCooldownRegistry.MENU.cooldown(user.getUniqueId());
    }
    return true;
  }

  @Override
  public @NotNull LiteralNode createCommandStructure() {
    return LiteralNode.node().markShouldExecuteCommand();
  }
}
