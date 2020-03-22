package com.mrivanplays.skins;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.core.MojangResponseHolder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandSkinSet implements TabExecutor {

  private final SkinsBukkitPlugin plugin;
  private final Map<UUID, Long> cooldownMap = new HashMap<>();

  public CommandSkinSet(SkinsBukkitPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String s,
      @NotNull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(plugin.color(plugin.getConfig().getString("messages.no-console")));
      return true;
    }
    Player player = (Player) sender;
    if (args.length == 0) {
      player.sendMessage(plugin.color(plugin.getConfig().getString("messages.command-usage")));
      return true;
    }
    Long timeLeft = cooldownMap.get(player.getUniqueId());
    if (timeLeft != null) {
      long remainingTime = (timeLeft - System.currentTimeMillis()) / 1000;
      if (remainingTime > 0) {
        player.sendMessage(
            plugin.color(
                plugin
                    .getConfig()
                    .getString("messages.cooldown")
                    .replace("%timeLeft%", Long.toString(remainingTime))));
        return true;
      }
    }
    MojangResponseHolder responseHolder = plugin.getApi().getSkinHolder(args[0]);
    if (responseHolder.isJustFetched()) {
      if (!responseHolder.getResponse().getSkin().isPresent()) {
        player.sendMessage(plugin.color(plugin.getConfig().getString("messages.not-premium")));
      } else {
        plugin.getApi().setSkin(player, responseHolder.getResponse());
        player.sendMessage(
            plugin.color(plugin.getConfig().getString("messages.skin-set-successfully")));
      }
    } else {
      if (!responseHolder.getResponse().getSkin().isPresent()) {
        player.sendMessage(plugin.color(plugin.getConfig().getString("messages.not-premium")));
      } else {
        Skin skin = responseHolder.getResponse().getSkin().get();
        Skin setSkin = checkForSkinUpdate(args[0], skin);
        plugin.getApi().setSkin(player, setSkin, args[0]);
        player.sendMessage(
            plugin.color(plugin.getConfig().getString("messages.skin-set-successfully")));
      }
    }
    long cooldown = 1000 * 30;
    cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(
      @NotNull CommandSender commandSender,
      @NotNull Command command,
      @NotNull String s,
      @NotNull String[] strings) {
    return Collections.emptyList();
  }

  private Skin checkForSkinUpdate(String name, Skin skin) {
    MojangResponse response = plugin.getSkinFetcher().apiFetch(name, skin.getOwner()).join();
    if (response.getSkin().isPresent()) {
      Skin fetched = response.getSkin().get();
      if (skin.getTexture().equalsIgnoreCase(fetched.getTexture())) {
        return skin;
      } else {
        return fetched;
      }
    } else {
      return skin;
    }
  }
}
