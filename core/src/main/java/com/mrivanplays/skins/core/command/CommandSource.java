package com.mrivanplays.skins.core.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public interface CommandSource {

  void sendMessage(Component message);

  default void sendMessage(String message) {
    // hack!
    TextComponent comp = LegacyComponentSerializer.legacy('&').deserialize(message);
    MiniMessage miniMessage = MiniMessage.markdown();
    sendMessage(miniMessage.deserialize(miniMessage.serialize(comp)));
  }
}
