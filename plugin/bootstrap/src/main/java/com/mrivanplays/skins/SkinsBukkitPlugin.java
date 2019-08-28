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

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.bukkit.SkinsBukkit;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.paper.SkinsPaper;
import com.mrivanplays.skins.protocolsupport.ProtocolSupportSkinSetter;
import com.mrivanplays.skins.protocolsupport.SkinsProtocolSupport;
import io.papermc.lib.PaperLib;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class SkinsBukkitPlugin extends JavaPlugin {

    private SkinsApi api;
    private boolean disabled = false;

    @Override
    public void onLoad() {
        new MetricsLite(this);
        PaperLib.suggestPaper(this);
        if (getServer().getPluginManager().getPlugin("ProtocolSupport") != null) {
            getLogger().warning("You are running ProtocolSupport! Disabling /skinset ...");
            SkinsProtocolSupport plugin = new SkinsProtocolSupport();
            plugin.enable(getDataFolder());
            api = plugin.getApi();
            getServer().getPluginManager()
                    .registerEvents(new ProtocolSupportSkinSetter(((AbstractSkinsApi) api).getSkinFetcher()), this);
        } else {
            if (!PaperLib.isPaper()) {
                SkinsBukkit skinsBukkit = new SkinsBukkit();
                skinsBukkit.enable(getDataFolder());
                if (skinsBukkit.getSkinSetter() == null) {
                    getLogger().severe("You are running unsupported minecraft version, disabling...");
                    disabled = true;
                    return;
                }
                api = skinsBukkit.getApi();
            } else {
                String version = getServer().getVersion();
                if (!version.contains("1.13.2") && !version.contains("1.14")) {
                    getLogger().severe("You are running unsupported minecraft version, disabling...");
                    disabled = true;
                    return;
                }
                SkinsPaper paper = new SkinsPaper();
                paper.enable(getDataFolder());
                api = paper.getApi();
            }
        }
        getServer().getServicesManager().register(SkinsApi.class, api, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        if (disabled) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        PaperLib.suggestPaper(this);
        saveDefaultConfig();
        CommandSkinSet commandSkinSet = new CommandSkinSet(this);
        getCommand("skinset").setExecutor(commandSkinSet);
        getCommand("skinset").setTabCompleter(commandSkinSet);
        CommandSkinReload commandSkinReload = new CommandSkinReload(this);
        getCommand("skinreload").setExecutor(commandSkinReload);
        getCommand("skinreload").setTabCompleter(commandSkinReload);
        if (!api.isRunningProtocolSupport()) {
            getServer().getPluginManager().registerEvents(new DefaultSkinSetListener(this), this);
            getLogger().info("Running on " + PaperLib.getEnvironment().getName());
        } else {
            getLogger().info("Running on " + PaperLib.getEnvironment().getName() + " & ProtocolSupport");
        }
    }

    public SkinsApi getApi() {
        return api;
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
