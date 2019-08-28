/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)
 * Copyright 2019 contributors

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.mrivanplays.skins;

import com.mrivanplays.skins.api.MojangResponse;
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
            @NotNull String[] args
    ) {
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
                player.sendMessage(plugin.color(plugin.getConfig()
                        .getString("messages.cooldown")
                        .replace("%timeLeft%", Long.toString(remainingTime))));
                return true;
            }
        }
        MojangResponse response = plugin.getApi().getSkin(args[0]);
        if (!response.getSkin().isPresent()) {
            player.sendMessage(plugin.color(plugin.getConfig().getString("messages.not-premium")));
            return true;
        }
        plugin.getApi().setSkin(player, response.getSkin().get(), args[0]);
        player.sendMessage(plugin.color(plugin.getConfig().getString("messages.skin-set-successfully")));
        long cooldown = 1000 * 30;
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
        return true;
    }

    @Override
    public @Nullable
    List<String> onTabComplete(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] strings
    ) {
        return Collections.emptyList();
    }
}
