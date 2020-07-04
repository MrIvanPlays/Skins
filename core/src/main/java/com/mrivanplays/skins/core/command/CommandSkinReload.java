package com.mrivanplays.skins.core.command;

import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class CommandSkinReload implements Command {

  @Override
  public void execute(CommandSource source, String[] args) {
    source.sendMessage(
        TextComponent.of(
                "This command has been removed in v2.0.0 . The reason is that dynamic reloads may cause various of types of issues.")
            .color(NamedTextColor.RED));
  }

  @Override
  public List<String> complete(CommandSource source, String[] args) {
    return Collections.emptyList();
  }
}
