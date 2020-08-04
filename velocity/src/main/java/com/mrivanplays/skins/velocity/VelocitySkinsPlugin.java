package com.mrivanplays.skins.velocity;

import com.mrivanplays.commandworker.core.Command;
import com.mrivanplays.commandworker.velocity.VelocityCommandManager;
import com.mrivanplays.skins.api.SkinsInfo;
import com.mrivanplays.skins.core.AbstractSkinsPlugin;
import com.mrivanplays.skins.core.Logger;
import com.mrivanplays.skins.core.Scheduler;
import com.mrivanplays.skins.core.SkinsConfiguration;
import com.mrivanplays.skins.core.SkinsUser;
import com.mrivanplays.skins.core.command.CommandSource;
import com.mrivanplays.skins.core.dependency.classloader.PluginClassLoader;
import com.mrivanplays.skins.core.util.SkinsInfoParser;
import com.velocitypowered.api.proxy.Player;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VelocitySkinsPlugin extends AbstractSkinsPlugin {

  private final VelocityPlugin plugin;
  private final Logger logger;
  private Scheduler scheduler;
  private SkinsInfo info;
  private PluginClassLoader pluginClassLoader;
  private SkinsConfiguration configuration;

  private final VelocityCommandSourceManager sourceManager;

  private Map<UUID, SkinsUser> userCache = new HashMap<>();
  private VelocityCommandManager commandManager;

  public VelocitySkinsPlugin(VelocityPlugin plugin) {
    this.plugin = plugin;
    this.logger = new VelocityLogger(plugin.getLogger());
    this.sourceManager = new VelocityCommandSourceManager(this);
  }

  @Override
  public void enable() {
    scheduler = new Scheduler.Default(Runnable::run);
    configuration =
        new SkinsConfiguration(new VelocityConfigurationAdapter(plugin, this::getResourceStream));

    String version =
        plugin
            .getProxy()
            .getPluginManager()
            .fromInstance(plugin)
            .get()
            .getDescription()
            .getVersion()
            .get();
    String implementationVersion = plugin.getClass().getPackage().getImplementationVersion();
    info =
        SkinsInfoParser.parseInfo(
            version, implementationVersion, getLogger(), new VelocityEnvironment());
    pluginClassLoader = new VelocityPluginClassLoader(plugin);
    commandManager = new VelocityCommandManager(plugin.getProxy());

    super.enable();
  }

  @Override
  public SkinsUser obtainUser(String name) {
    Optional<Player> playerOpt = plugin.getProxy().getPlayer(name);
    if (!playerOpt.isPresent()) {
      return new VelocitySkinsOfflineUser(name, null, this);
    }
    Player player = playerOpt.get();
    if (userCache.containsKey(player.getUniqueId())) {
      return userCache.get(player.getUniqueId());
    }
    VelocitySkinsUser user = new VelocitySkinsUser(player, this);
    userCache.put(player.getUniqueId(), user);
    return user;
  }

  @Override
  public SkinsUser obtainUser(UUID uuid) {
    Optional<Player> playerOpt = plugin.getProxy().getPlayer(uuid);
    if (!playerOpt.isPresent()) {
      return new VelocitySkinsOfflineUser(
          userCache.containsKey(uuid) ? userCache.get(uuid).getName() : null, uuid, this);
    }
    Player player = playerOpt.get();
    if (userCache.containsKey(player.getUniqueId())) {
      return userCache.get(player.getUniqueId());
    }
    VelocitySkinsUser user = new VelocitySkinsUser(player, this);
    userCache.put(player.getUniqueId(), user);
    return user;
  }

  public void removeFromUserCache(UUID uuid) {
    userCache.remove(uuid);
  }

  public SkinsUser obtainUser(String name, UUID uniqueId) {
    Optional<Player> playerOpt = plugin.getProxy().getPlayer(uniqueId);
    if (!playerOpt.isPresent()) {
      return new VelocitySkinsOfflineUser(name, uniqueId, this);
    }
    Player player = playerOpt.get();
    if (userCache.containsKey(player.getUniqueId())) {
      return userCache.get(player.getUniqueId());
    }
    VelocitySkinsUser user = new VelocitySkinsUser(player, this);
    userCache.put(player.getUniqueId(), user);
    return user;
  }

  @Override
  public SkinsInfo getInfo() {
    return info;
  }

  @Override
  public void registerCommand(
      String name, Predicate<CommandSource> permissionCheck, Command<CommandSource> command) {
    commandManager.register(
        new VelocityCommandWrapper(command, sourceManager),
        (sender) -> permissionCheck.test(sourceManager.obtainSource(sender)),
        name);
  }

  @Override
  public Logger getLogger() {
    return logger;
  }

  @Override
  public PluginClassLoader getPluginClassLoader() {
    return pluginClassLoader;
  }

  @Override
  public Path getDataDirectory() {
    return plugin.getDataDirectory().toAbsolutePath();
  }

  @Override
  public Scheduler getScheduler() {
    return scheduler;
  }

  @Override
  public SkinsConfiguration getConfiguration() {
    return configuration;
  }

  @Override
  public String identifyClassLoader(ClassLoader classLoader) {
    return null;
  }

  @Override
  public InputStream getResourceStream(String name) {
    return getClass().getClassLoader().getResourceAsStream(name);
  }

  @Override
  public List<String> allPlayersForCompletions() {
    return plugin.getProxy().getAllPlayers().stream()
        .map(Player::getUsername)
        .collect(Collectors.toList());
  }
}
