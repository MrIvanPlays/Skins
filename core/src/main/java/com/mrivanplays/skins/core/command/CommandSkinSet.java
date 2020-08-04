package com.mrivanplays.skins.core.command;

import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.api.SkinsApiProvider;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.UserCooldownRegistry;
import com.mrivanplays.skins.core.storage.StoredSkin;
import com.mrivanplays.skins.core.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    AbstractSkinsUser user = (AbstractSkinsUser) plugin.obtainUser(source.getName());
    if (args.length == 0) {
      user.sendMessage(messages.getCommandUsage());
      return;
    }
    SkinsApi api = SkinsApiProvider.get();
    api.getSkin(args[0]).thenAccept(skinOpt -> dispatchSkinSet(user, skinOpt, args[0]));
  }

  public void dispatchSkinSet(AbstractSkinsUser user, Optional<Skin> skinOpt, String name) {
    SkinsConfiguration.Messages messages = plugin.getConfiguration().getMessages();
    long cooldownedLeft = UserCooldownRegistry.SKIN_SET.getTimeLeft(user.getUniqueId());
    if (cooldownedLeft > 0) {
      user.sendMessage(messages.getCooldown().replace("%timeLeft%", Long.toString(cooldownedLeft)));
      return;
    }
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
              user.setSkin(skin, name, false);
              user.sendMessage(messages.getSkinSetSuccessfully());
              UserCooldownRegistry.SKIN_SET.cooldown(user.getUniqueId());
            });
  }

  @Override
  public List<String> complete(CommandSource source, String[] args) {
    if (args.length == 1) {
      List<String> matches = new ArrayList<>();
      List<StoredSkin> skins = plugin.getStorage().all().join();
      matches.addAll(Utils.map(skins, StoredSkin::getOwnerName));

      matches.addAll(plugin.allPlayersForCompletions());
      return matches.stream()
          .filter(match -> match.toLowerCase().startsWith(args[0].toLowerCase()))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
