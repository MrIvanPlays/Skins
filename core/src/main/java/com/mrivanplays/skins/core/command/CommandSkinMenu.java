package com.mrivanplays.skins.core.command;

import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkinsUser;
import com.mrivanplays.skins.core.UserCooldownRegistry;
import java.util.Collections;
import java.util.List;

public class CommandSkinMenu implements Command {

  private final SkinsPlugin plugin;

  public CommandSkinMenu(SkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void execute(CommandSource source, String[] args) {
    SkinsConfiguration.Messages messages = plugin.getConfiguration().getMessages();
    if (!source.isPlayer()) {
      source.sendMessage(messages.getNoConsole());
      return;
    }
    SkinsUser user = plugin.obtainUser(source.getName());
    long cooldownRemaining = UserCooldownRegistry.MENU.getTimeLeft(user.getUniqueId());
    if (cooldownRemaining > 0) {
      user.sendMessage(
          messages.getCooldown().replace("%timeLeft%", Long.toString(cooldownRemaining)));
      return;
    }
    if (user.openSkinMenu()) {
      UserCooldownRegistry.MENU.cooldown(user.getUniqueId());
    }
  }

  @Override
  public List<String> complete(CommandSource source, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public boolean hasPermission(CommandSource source) {
    return source.hasPermission("skins.menu");
  }
}
