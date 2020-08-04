package com.mrivanplays.skins.bukkit;

import com.mrivanplays.commandworker.bukkit.BukkitCommandManager;
import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.skins.api.Environment;
import com.mrivanplays.skins.api.SkinsInfo;
import com.mrivanplays.skins.bukkit.core.GeneralSkinsPlugin;
import com.mrivanplays.skins.bukkit.menuadapters.BukkitSMAdapter;
import com.mrivanplays.skins.bukkit.paper.PaperUser;
import com.mrivanplays.skins.bukkit.protocolsupport.ProtocolSupportUser;
import com.mrivanplays.skins.bukkit_general.SkinsMenu;
import com.mrivanplays.skins.bukkit_general.SkinsMenuAdapter;
import com.mrivanplays.skins.core.AbstractSkinsUser;
import com.mrivanplays.skins.core.Logger;
import com.mrivanplays.skins.core.SkinsUser;
import com.mrivanplays.skins.core.command.CommandSource;
import com.mrivanplays.skins.core.dependency.classloader.PluginClassLoader;
import com.mrivanplays.skins.core.dependency.classloader.ReflectionClassLoader;
import com.mrivanplays.skins.core.util.SkinsInfoParser;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BukkitSkinsPlugin extends GeneralSkinsPlugin {

  private final SkinsBukkitPlugin parent;
  private SkinsInfo info;

  private Map<UUID, SkinsUser> userMap = new HashMap<>();
  private final CommandSourceManager sourceManager;
  private SkinsMenu skinsMenu;
  private BukkitCommandManager commandManager;
  private PluginClassLoader classLoader;
  private final Logger logger;

  public BukkitSkinsPlugin(SkinsBukkitPlugin parent) {
    super(parent);
    this.parent = parent;
    this.sourceManager = new CommandSourceManager(this);
    this.logger = new BukkitLogger(parent.getLogger());
  }

  @Override
  public void enable() {
    String version = parent.getDescription().getVersion();
    String implementationVersion = parent.getClass().getPackage().getImplementationVersion();
    info =
        SkinsInfoParser.parseInfo(
            version, implementationVersion, getLogger(), EnvironmentInitializer.get());
    commandManager = new BukkitCommandManager(parent);
    this.classLoader = new ReflectionClassLoader(parent, parent.getLogger());
    super.enable();
    SkinsMenuAdapter menuAdapter = new BukkitSMAdapter(this);
    this.skinsMenu = new SkinsMenu(menuAdapter, parent);
  }

  @Override
  public SkinsUser obtainUser(String name) {
    OfflinePlayer initializer;
    Player player = Bukkit.getPlayer(name);
    if (player == null) {
      // fuck you bukkit
      //noinspection deprecation
      initializer = Bukkit.getOfflinePlayer(name);
    } else {
      initializer = player;
    }
    if (userMap.containsKey(initializer.getUniqueId())) {
      return userMap.get(initializer.getUniqueId());
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
    userMap.put(initializer.getUniqueId(), user);
    return user;
  }

  public AbstractSkinsUser obtainUser(Player player) {
    if (userMap.containsKey(player.getUniqueId())) {
      return (AbstractSkinsUser) userMap.get(player.getUniqueId());
    }
    AbstractSkinsUser user;
    Environment env = info.getEnvironment();
    if (env.paper()) {
      user = new PaperUser(this, skinsMenu, player);
    } else if (env.protocolSupport()) {
      user = new ProtocolSupportUser(this, skinsMenu, player);
    } else {
      user = new CraftBukkitUser(this, skinsMenu, player);
    }
    userMap.put(player.getUniqueId(), user);
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
    if (userMap.containsKey(uuid)) {
      return userMap.get(uuid);
    }
    SkinsUser user;
    Environment env = info.getEnvironment();
    if (env.paper()) {
      user = new PaperUser(this, skinsMenu, player);
    } else if (env.protocolSupport()) {
      user = new ProtocolSupportUser(this, skinsMenu, player);
    } else {
      user = new CraftBukkitUser(this, skinsMenu, player);
    }
    userMap.put(initializer.getUniqueId(), user);
    return user;
  }

  public void removeFromUserMap(UUID uuid) {
    userMap.remove(uuid);
  }

  @Override
  public SkinsInfo getInfo() {
    return info;
  }

  @Override
  public void registerCommand(
      String name, Predicate<CommandSource> permissionCheck, Command<CommandSource> command) {
    commandManager.register(
        new BukkitCommandWrapper(command, sourceManager),
        (sender) -> permissionCheck.test(sourceManager.obtain(sender)),
        name);
  }

  @Override
  public Logger getLogger() {
    return logger;
  }

  @Override
  public PluginClassLoader getPluginClassLoader() {
    return classLoader;
  }
}
