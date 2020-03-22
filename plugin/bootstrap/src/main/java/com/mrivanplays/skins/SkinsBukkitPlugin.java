package com.mrivanplays.skins;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.bukkit.SkinsBukkit;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.SupportedVersions;
import com.mrivanplays.skins.bukkit.abstraction.handle.SkinSetterHandler;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.InitializationData;
import com.mrivanplays.skins.core.MojangDataProvider;
import com.mrivanplays.skins.core.SkinFetcher;
import com.mrivanplays.skins.core.storage.SkinStorage;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import com.mrivanplays.skins.core.storage.YamlSkinStorage;
import com.mrivanplays.skins.paper.SkinsPaper;
import com.mrivanplays.skins.protocolsupport.ProtocolSupportSkinSetter;
import com.mrivanplays.skins.protocolsupport.SkinsProtocolSupport;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class SkinsBukkitPlugin extends JavaPlugin {

  private SkinsApi api;
  private boolean disabled = false;
  private SkinStorage skinStorage;
  private SkinFetcher skinFetcher;
  private AbstractSkinsApi abstractSkinsApi;
  private SkinsMenu skinsMenu;
  private Instant startTime;

  @Override
  public void onLoad() {
    startTime = Instant.now();
    new MetricsLite(this);
    SkinSetter skinSetter = SkinSetterHandler.getSkinSetter();
    Function<SkullItemBuilderData, ItemStack> transformer =
        data -> {
          MojangResponse response = data.getOwner();
          Skin skin = response.getSkin().orElse(null);
          return skinSetter.getMenuItem(
              skin, response.getNickname(), data.getItemName(), data.getItemLore());
        };
    skinStorage = new YamlSkinStorage(getDataFolder());
    InitializationData initializationData =
        new InitializationData(
            skinStorage,
            transformer,
            new MojangDataProvider(getLogger()));

    if (!Platform.isPaper()) {
      getLogger().warning("Skins works better if you run Paper!");
    }
    if (isProtocolSupport()) {
      getLogger().warning("You are running ProtocolSupport! Applying modifications to skin sets");
      SkinsProtocolSupport plugin = new SkinsProtocolSupport();
      plugin.enable(initializationData);
      api = plugin.getApi();
    } else {
      if (!Platform.isPaper()) {
        SkinsBukkit skinsBukkit = new SkinsBukkit();
        skinsBukkit.enable(initializationData);
        if (skinSetter == null) {
          getLogger().severe("You are running unsupported minecraft version, disabling...");
          disabled = true;
          return;
        }
        api = skinsBukkit.getApi();
      } else {
        if (!SupportedVersions.isCurrentSupported()) {
          getLogger().severe("You are running unsupported minecraft version, disabling...");
          disabled = true;
          return;
        }
        SkinsPaper paper = new SkinsPaper();
        paper.enable(initializationData);
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
    saveDefaultConfig();
    abstractSkinsApi = (AbstractSkinsApi) api;
    skinFetcher = abstractSkinsApi.getSkinFetcher();
    skinFetcher.startUpdateCheck(this, 1800); // 1800 seconds - 30 minutes
    getCommand("skinset").setExecutor(new CommandSkinSet(this));
    getCommand("skinreload").setExecutor(new CommandSkinReload(this));
    skinsMenu = new SkinsMenu(this);
    getCommand("skinmenu").setExecutor(new CommandSkinMenu(this));
    if (!isProtocolSupport()) {
      getServer().getPluginManager().registerEvents(new DefaultSkinSetListener(this), this);
      getLogger().info("ENVIRONMENT: " + Platform.TYPE.name());
    } else {
      getServer()
          .getPluginManager()
          .registerEvents(new ProtocolSupportSkinSetter(abstractSkinsApi), this);
      getLogger().info("ENVIRONMENT: " + Platform.TYPE.name() + " & PROTOCOLSUPPORT");
    }
    String version = Bukkit.getVersion();
    String minecraftVersion = version.substring(version.indexOf('('), version.indexOf(')')).replace("MC: ", "");
    getLogger().info("MINECRAFT VERSION: " + minecraftVersion);
    new UpdateCheckerSetup(this, "skins.updatenotify").setup();
    Duration timeTaken = Duration.between(startTime, Instant.now());
    getLogger().info("Successfully enabled. (took " + timeTaken.toMillis() + " ms)");
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

  public SkinsMenu getSkinsMenu() {
    return skinsMenu;
  }

  public void reload() {
    this.reloadConfig();
    this.skinsMenu = new SkinsMenu(this);
  }
}
