package com.mrivanplays.skins.bukkit;

import com.mrivanplays.annotationconfig.yaml.YamlConfig;
import com.mrivanplays.skins.api.Environment;
import com.mrivanplays.skins.api.SkinsInfo;
import com.mrivanplays.skins.bukkit.core.CommandMapRetriever;
import com.mrivanplays.skins.bukkit.core.GeneralSkinsPlugin;
import com.mrivanplays.skins.bukkit.paper.PaperCommandMapRetriever;
import com.mrivanplays.skins.bukkit.paper.PaperUser;
import com.mrivanplays.skins.bukkit.protocolsupport.ProtocolSupportUser;
import com.mrivanplays.skins.bukkit_general.SkinsMenu;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsUser;
import com.mrivanplays.skins.core.command.Command;
import com.mrivanplays.skins.core.dependency.classloader.PluginClassLoader;
import com.mrivanplays.skins.core.dependency.classloader.ReflectionClassLoader;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

public class BukkitSkinsPlugin extends GeneralSkinsPlugin {

  private final SkinsBukkitPlugin parent;
  private SkinsInfo info;

  private Map<String, SkinsUser> userMap = new HashMap<>();
  private final CommandSourceManager sourceManager;
  private SkinsMenu skinsMenu;
  private CommandMap commandMap;
  private String noPermissionMessage;
  private PluginClassLoader classLoader;

  public BukkitSkinsPlugin(SkinsBukkitPlugin parent) {
    super(parent);
    this.parent = parent;
    this.sourceManager = new CommandSourceManager(this);
  }

  @Override
  public void enable() {
    String version = parent.getDescription().getVersion();
    String implementationVersion = parent.getClass().getPackage().getImplementationVersion();
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
    info = new SkinsInfo(version, commit, buildNumber, EnvironmentInitializer.get());
    CommandMapRetriever commandMapRetriever;
    if (info.getEnvironment().paper()) {
      commandMapRetriever = new PaperCommandMapRetriever();
    } else {
      commandMapRetriever = new CommandMapRetriever();
    }
    this.commandMap = commandMapRetriever.retrieveCommandMap();
    this.classLoader = new ReflectionClassLoader(parent, parent.getLogger());
    super.enable();
    this.skinsMenu = new SkinsMenu(this, parent);
  }

  @Override
  public void afterConfigGeneration() {
    this.noPermissionMessage = getConfiguration().getMessages().getNoPermission();
  }

  @Override
  public void generateConfig(SkinsConfiguration config) {
    File configFile = new File(parent.getDataFolder(), "config.yml");
    YamlConfig.load(config, configFile);
  }

  @Override
  public SkinsUser obtainUser(String name) {
    if (userMap.containsKey(name)) {
      return userMap.get(name);
    }
    OfflinePlayer initializer;
    Player player = Bukkit.getPlayer(name);
    if (player == null) {
      // fuck you bukkit
      //noinspection deprecation
      initializer = Bukkit.getOfflinePlayer(name);
    } else {
      initializer = player;
    }
    SkinsUser user;
    Environment env = info.getEnvironment();
    if (env.paper()) {
      user = new PaperUser(this, skinsMenu, initializer);
    } else if (env.protocolSupport()) {
      user = new ProtocolSupportUser(this, skinsMenu, initializer);
    } else {
      user = new CraftBukkitUser(this, skinsMenu, initializer);
    }
    userMap.put(name, user);
    return user;
  }

  @Override
  public SkinsUser obtainUser(UUID uuid) {
    OfflinePlayer initializer;
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      initializer = Bukkit.getOfflinePlayer(uuid);
    } else {
      initializer = player;
    }
    return obtainUser(initializer.getName());
  }

  @Override
  public SkinsInfo getInfo() {
    return info;
  }

  @Override
  public void registerCommand(String name, Command command) {
    commandMap.register(
        name,
        parent.getName(),
        new CommandRegistration(name, command, sourceManager, noPermissionMessage));
  }

  @Override
  public Logger getLogger() {
    return parent.getLogger();
  }

  @Override
  public PluginClassLoader getPluginClassLoader() {
    return classLoader;
  }

  @Override
  public Path getDataDirectory() {
    return parent.getDataFolder().toPath().toAbsolutePath();
  }

  @Override
  public InputStream getResourceStream(String name) {
    return parent.getResource(name);
  }
}
