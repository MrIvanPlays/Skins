package com.mrivanplays.skins.velocity;

import com.mrivanplays.skins.core.SkinsUser;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.util.GameProfile.Property;
import java.util.Collections;

public class GameProfileRequestListener implements EventHandler<GameProfileRequestEvent> {

  public static void register(VelocityPlugin bootstrap, VelocitySkinsPlugin plugin) {
    GameProfileRequestListener listener = new GameProfileRequestListener(plugin);
    bootstrap
        .getProxy()
        .getEventManager()
        .register(bootstrap, GameProfileRequestEvent.class, listener);
  }

  private final VelocitySkinsPlugin plugin;

  public GameProfileRequestListener(VelocitySkinsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void execute(GameProfileRequestEvent event) {
    SkinsUser user = plugin.obtainUser(event.getUsername(), event.getGameProfile().getId());
    user.getSkin()
        .join()
        .ifPresent(
            skin ->
                event.setGameProfile(
                    event
                        .getGameProfile()
                        .withProperties(
                            Collections.singletonList(
                                new Property(
                                    "textures", skin.getTexture(), skin.getSignature())))));
  }
}
