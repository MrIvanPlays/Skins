package com.mrivanplays.skins.core.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.commandworker.core.LiteralNode;
import com.mrivanplays.commandworker.core.argument.RequiredArgument;
import com.mrivanplays.commandworker.core.argument.parser.ArgumentHolder;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.api.SkinsApiProvider;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.UserCooldownRegistry;
import com.mrivanplays.skins.core.storage.StoredSkin;
import com.mrivanplays.skins.core.util.Utils;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class CommandSkinSet implements Command<CommandSource> {

  private final SkinsPlugin plugin;

  public CommandSkinSet(SkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean execute(
      @NotNull CommandSource source, @NotNull String label, @NotNull ArgumentHolder args)
      throws CommandSyntaxException {
    SkinsConfiguration.Messages messages = plugin.getConfiguration().getMessages();
    if (!source.isPlayer()) {
      source.sendMessage(messages.getNoConsole());
      return true;
    }
    AbstractSkinsUser user = (AbstractSkinsUser) plugin.obtainUser(source.getName());
    String skin = args.getRequiredArgument("skinName", String.class);
    if (skin == null) {
      throw syntaxException("Usage: " + args.buildUsage(label));
    }
    SkinsApi api = SkinsApiProvider.get();
    api.getSkin(skin).thenAccept(skinOpt -> dispatchSkinSet(user, skinOpt, skin));
    return true;
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
  public @NotNull LiteralNode createCommandStructure() {
    return LiteralNode.node()
        .argument(
            RequiredArgument.argument("skinName", StringArgumentType.word())
                .suggests(
                    builder -> {
                      String remaining = builder.getRemaining();
                      Set<String> matches = new HashSet<>();
                      List<StoredSkin> skins = plugin.getStorage().all().join();
                      matches.addAll(Utils.map(skins, StoredSkin::getOwnerName));
                      matches.addAll(plugin.allPlayersForCompletions());

                      if (remaining.isEmpty()) {
                        for (String match : matches) {
                          builder.suggest(match, new LiteralMessage("The skin of Player " + match));
                        }
                        return;
                      }

                      for (String match : matches) {
                        if (match.toLowerCase().startsWith(remaining.toLowerCase())) {
                          builder.suggest(match, new LiteralMessage("The skin of Player " + match));
                        }
                      }
                    }));
  }
}
