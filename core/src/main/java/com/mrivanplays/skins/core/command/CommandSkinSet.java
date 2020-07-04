package com.mrivanplays.skins.core.command;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.api.SkinsApiProvider;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.UserCooldown;
import com.mrivanplays.skins.core.storage.StoredSkin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandSkinSet implements Command {

  private final SkinsPlugin plugin;

  public CommandSkinSet(SkinsPlugin plugin) {
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
              if (args.length == 0) {
                user.sendMessage(messages.getCommandUsage());
                return;
              }
              long cooldownedLeft =
                  UserCooldown.getGlobalInstance().getTimeLeft(user.getUniqueId());
              if (cooldownedLeft > 0) {
                user.sendMessage(
                    messages.getCooldown().replace("%timeLeft%", Long.toString(cooldownedLeft)));
                return;
              }
              SkinsApi api = SkinsApiProvider.get();
              api.getSkin(args[0])
                  .thenAccept(
                      skinOpt -> {
                        if (!skinOpt.isPresent()) {
                          user.sendMessage(messages.getNotPremium());
                          return;
                        }
                        Skin skin = skinOpt.get();
                        user.getSkin()
                            .thenAccept(
                                currentSkinOpt -> {
                                  if (currentSkinOpt.isPresent()) {
                                    Skin current = currentSkinOpt.get();
                                    if (skin.equals(current)) {
                                      user.sendMessage(messages.getSkinAlreadySet());
                                      return;
                                    }
                                  }
                                  user.setSkin(skin);
                                  user.sendMessage(messages.getSkinSetSuccessfully());
                                  UserCooldown.getGlobalInstance().cooldown(user.getUniqueId());
                                });
                      });
            });
  }

  @Override
  public List<String> complete(CommandSource source, String[] args) {
    if (args.length == 1) {
      List<String> matches = new ArrayList<>();
      plugin
          .getStorage()
          .all()
          .thenAccept(
              skins ->
                  matches.addAll(
                      skins.stream().map(StoredSkin::getOwnerName).collect(Collectors.toList())));

      matches.addAll(plugin.allPlayersForCompletions());
      return matches.stream()
          .filter(match -> match.toLowerCase().startsWith(args[0].toLowerCase()))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
