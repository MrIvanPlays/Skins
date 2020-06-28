package com.mrivanplays.skins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandSkinInfo implements CommandExecutor {

  private SkinsBukkitPlugin plugin;

  public CommandSkinInfo(SkinsBukkitPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    if (!sender.hasPermission("skins.info")) {
      sender.sendMessage(plugin.color(plugin.getConfig().getString("messages.no-permission")));
      return true;
    }
    String implementationVersion = plugin.getClass().getPackage().getImplementationVersion();
    String version = plugin.getDescription().getVersion();
    sender.sendMessage("Skins v" + version + " by MrIvanPlays");
    sender.sendMessage("Exact version: " + implementationVersion);
    if (version.contains("SNAPSHOT")) {
      sender
          .spigot()
          .sendMessage(
              new ComponentBuilder(
                      "THIS IS A DEVELOPMENT BUILD! EVERY BUG SHOULD BE REPORTED TO THE ISSUE TRACKER !!!!")
                  .color(ChatColor.RED)
                  .create());
    } else {
      sender
          .spigot()
          .sendMessage(
              new ComponentBuilder("You are running a version which is considered stable.")
                  .color(ChatColor.GREEN)
                  .create());
    }
    return true;
  }
}
