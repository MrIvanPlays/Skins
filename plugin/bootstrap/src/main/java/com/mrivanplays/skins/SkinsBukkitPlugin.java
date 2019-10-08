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

import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.bukkit.SkinsBukkit;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.SkinFetcher;
import com.mrivanplays.skins.core.SkinStorage;
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
  private SkinStorage skinStorage;
  private SkinFetcher skinFetcher;
  private AbstractSkinsApi abstractSkinsApi;

  @Override
  public void onLoad() {
    new MetricsLite(this);
    PaperLib.suggestPaper(this);
    if (isProtocolSupport()) {
      getLogger().warning("You are running ProtocolSupport! Applying modifications to skin sets");
      SkinsProtocolSupport plugin = new SkinsProtocolSupport();
      plugin.enable(getDataFolder());
      api = plugin.getApi();
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
    abstractSkinsApi = (AbstractSkinsApi) api;
    skinFetcher = abstractSkinsApi.getSkinFetcher();
    skinStorage = abstractSkinsApi.getSkinStorage();
    CommandSkinSet commandSkinSet = new CommandSkinSet(this);
    getCommand("skinset").setExecutor(commandSkinSet);
    getCommand("skinset").setTabCompleter(commandSkinSet);
    CommandSkinReload commandSkinReload = new CommandSkinReload(this);
    getCommand("skinreload").setExecutor(commandSkinReload);
    getCommand("skinreload").setTabCompleter(commandSkinReload);
    if (!isProtocolSupport()) {
      getServer().getPluginManager().registerEvents(new DefaultSkinSetListener(this), this);
      getLogger().info("Running on " + PaperLib.getEnvironment().getName());
    } else {
      getServer()
          .getPluginManager()
          .registerEvents(new ProtocolSupportSkinSetter(abstractSkinsApi), this);
      getLogger().info("Running on " + PaperLib.getEnvironment().getName() + " & ProtocolSupport");
    }
    new UpdateCheckerSetup(this, "skins.updatenotify").setup();
  }

  public AbstractSkinsApi getApi() {
    return abstractSkinsApi;
  }

  public String color(String text) {
    return ChatColor.translateAlternateColorCodes('&', text);
  }

  public SkinStorage getSkinStorage() {
    return skinStorage;
  }

  public SkinFetcher getSkinFetcher() {
    return skinFetcher;
  }

  private boolean isProtocolSupport() {
    return getServer().getPluginManager().getPlugin("ProtocolSupport") != null;
  }
}
