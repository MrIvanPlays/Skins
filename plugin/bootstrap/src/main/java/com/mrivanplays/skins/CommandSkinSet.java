/*
    Copyright (C) 2019 Ivan Pekov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.mrivanplays.skins;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
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
        plugin.getApi().setSkin(player, checkForSkinUpdate(args[0], response.getSkin().get()), args[0]);
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

    private Skin checkForSkinUpdate(
            String name,
            Skin skin
    ) {
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
