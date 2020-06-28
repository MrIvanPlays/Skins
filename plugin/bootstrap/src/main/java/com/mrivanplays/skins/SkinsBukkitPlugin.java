package com.mrivanplays.skins;

import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.api.SkinsVersionInfo;
import com.mrivanplays.skins.bukkit.SkinsBukkit;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter;
import com.mrivanplays.skins.bukkit.abstraction.SkinSetter.SkullItemOwnerResponse;
import com.mrivanplays.skins.bukkit.abstraction.SupportedVersions;
import com.mrivanplays.skins.bukkit.abstraction.handle.SkinSetterHandler;
import com.mrivanplays.skins.core.AbstractSkinsApi;
import com.mrivanplays.skins.core.InitializationData;
import com.mrivanplays.skins.core.MojangDataProvider;
import com.mrivanplays.skins.core.SkinStorage;
import com.mrivanplays.skins.core.SkinsPlugin;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import com.mrivanplays.skins.paper.SkinsPaper;
import com.mrivanplays.skins.protocolsupport.ProtocolSupportSkinSetter;
import com.mrivanplays.skins.protocolsupport.SkinsProtocolSupport;
import java.util.Collections;
import java.util.function.Function;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class SkinsBukkitPlugin extends JavaPlugin {

  private SkinsApi api;
  private boolean disabled = false;
  private SkinStorage skinStorage;
  private AbstractSkinsApi abstractSkinsApi;
  private SkinsMenu skinsMenu;
  private CommandSkinSet skinSetCommand;

  @Override
  public void onLoad() {
    new MetricsLite(this);
    SkinSetter skinSetter = SkinSetterHandler.getSkinSetter();

    if (!Platform.isPaper()) {
      getLogger().warning("Skins works better if you run Paper!");
    }
    SkinsPlugin apiInitializer;
    if (isProtocolSupport()) {
      getLogger().warning("You are running ProtocolSupport! Applying modifications to skin sets");
      apiInitializer = new SkinsProtocolSupport();
    } else {
      if (!Platform.isPaper()) {
        apiInitializer = new SkinsBukkit();
        if (skinSetter == null) {
          getLogger().severe("You are running unsupported minecraft version, disabling...");
          disabled = true;
          return;
        }
      } else {
        if (!SupportedVersions.isCurrentSupported()) {
          getLogger().severe("You are running unsupported minecraft version, disabling...");
          disabled = true;
          return;
        }
        apiInitializer = new SkinsPaper();
      }
    }

    Function<SkullItemBuilderData, ItemStack> itemBuilderTransformer =
        data -> {
          MojangResponse response = data.getOwner();
          Skin skin = response.getSkin().orElse(null);
          return skinSetter.getMenuItem(
              skin, response.getNickname(), data.getItemName(), data.getItemLore());
        };
    Function<ItemStack, MojangResponse> skullOwnerTransformer =
        item -> {
          SkullItemOwnerResponse response = skinSetter.getSkullOwner(item);
          if (response == null) {
            return null;
          }
          return abstractSkinsApi.getSkin(response.name, response.uuid);
        };
    InitializationData initializationData =
        new InitializationData(
            getDataFolder(),
            itemBuilderTransformer,
            skullOwnerTransformer,
            new MojangDataProvider(getLogger()),
            initializeVersionInfo());
    apiInitializer.enable(initializationData);
    api = apiInitializer.getApi();
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
    skinStorage = abstractSkinsApi.getSkinStorage();

    final TabCompleter EMPTY = (sender, command, label, args) -> Collections.emptyList();
    skinSetCommand = new CommandSkinSet(this);
    getCommand("skinset").setExecutor(skinSetCommand);
    getCommand("skinreload").setExecutor(new CommandSkinReload(this));
    getCommand("skinreload").setTabCompleter(EMPTY);
    skinsMenu = new SkinsMenu(this);
    getCommand("skinmenu").setExecutor(new CommandSkinMenu(this));
    getCommand("skinmenu").setTabCompleter(EMPTY);
    getCommand("skininfo").setExecutor(new CommandSkinInfo(this));
    getCommand("skininfo").setTabCompleter(EMPTY);

    if (!isProtocolSupport()) {
      getServer().getPluginManager().registerEvents(new DefaultSkinSetListener(this), this);
      getLogger().info("Running on " + Platform.TYPE.getName());
    } else {
      getServer()
          .getPluginManager()
          .registerEvents(new ProtocolSupportSkinSetter(abstractSkinsApi), this);
      getLogger().info("Running on " + Platform.TYPE.getName() + " & ProtocolSupport");
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

  private boolean isProtocolSupport() {
    return getServer().getPluginManager().getPlugin("ProtocolSupport") != null;
  }

  public SkinsMenu getSkinsMenu() {
    return skinsMenu;
  }

  public CommandSkinSet getSkinSetCommand() {
    return skinSetCommand;
  }

  public void reload() {
    this.reloadConfig();
    this.skinsMenu = new SkinsMenu(this);
  }

  private SkinsVersionInfo initializeVersionInfo() {
    String version = getDescription().getVersion();
    // example of implementation version
    // git:Skins:1.1.6-SNAPSHOT:a5f217d:11
    String implementationVersion = getClass().getPackage().getImplementationVersion();
    String[] implVersionSplit = implementationVersion.split(":");
    String commit = implVersionSplit[3];
    String buildNumberPart = implVersionSplit[4];
    int buildNumber;
    if (buildNumberPart.equalsIgnoreCase("unknown")) {
      getLogger().warning("Could not detect proper build number, custom build?");
      buildNumber = -1;
    } else {
      buildNumber = Integer.parseInt(buildNumberPart);
    }
    return new SkinsVersionInfo(version, commit, buildNumber);
  }
}
