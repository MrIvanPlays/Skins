package com.mrivanplays.skins.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import org.slf4j.Logger;

public class VelocityPlugin {

  private final Logger logger;
  private final ProxyServer proxy;
  private final Path dataDirectory;

  private final VelocitySkinsPlugin plugin;

  @Inject
  public VelocityPlugin(Logger logger, ProxyServer proxy, @DataDirectory Path dataDirectory) {
    this.logger = logger;
    this.proxy = proxy;
    this.dataDirectory = dataDirectory;
    plugin = new VelocitySkinsPlugin(this);
  }

  @Subscribe
  public void initialize(ProxyInitializeEvent event) {
    try {
      Class.forName("com.velocitypowered.api.command.BrigadierCommand");
    } catch (ClassNotFoundException e) {
      logger.error("Skins requires at least Velocity 1.1.0 to function");
      logger.error("Please update in order to use Skins on Velocity");
      return;
    }
    plugin.enable();

    GameProfileRequestListener.register(this, plugin);
    DisconnectListener.register(this, plugin);

    logger.info("Running on Velocity");
  }

  @Subscribe
  public void terminate(ProxyShutdownEvent event) {
    plugin.disable();
  }

  public Logger getLogger() {
    return logger;
  }

  public ProxyServer getProxy() {
    return proxy;
  }

  public Path getDataDirectory() {
    return dataDirectory;
  }
}
