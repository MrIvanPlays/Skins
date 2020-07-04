package com.mrivanplays.skins.core.command;

import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.UserCooldown;
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
    plugin
        .obtainUser(source.getName())
        .exceptionally(
            e -> {
              e.printStackTrace();
              return null;
            })
        .thenAccept(
            user -> {
              long cooldownRemaining =
                  UserCooldown.getGlobalInstance().getTimeLeft(user.getUniqueId());
              if (cooldownRemaining > 0) {
                user.sendMessage(
                    messages.getCooldown().replace("%timeLeft%", Long.toString(cooldownRemaining)));
                return;
              }
              user.openSkinMenu();
              UserCooldown.getGlobalInstance().cooldown(user.getUniqueId());
            });
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
