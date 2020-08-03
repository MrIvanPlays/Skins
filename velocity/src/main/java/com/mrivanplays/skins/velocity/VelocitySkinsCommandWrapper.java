package com.mrivanplays.skins.velocity;

import com.mrivanplays.skins.core.command.Command;
import com.velocitypowered.api.command.SimpleCommand;
import java.util.List;

public class VelocitySkinsCommandWrapper implements SimpleCommand {

  private final Command parent;
  private final VelocityCommandSourceManager sourceManager;

  public VelocitySkinsCommandWrapper(Command parent, VelocityCommandSourceManager sourceManager) {
    this.parent = parent;
    this.sourceManager = sourceManager;
  }

  @Override
  public void execute(Invocation invocation) {
    parent.execute(sourceManager.obtainSource(invocation.source()), invocation.arguments());
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    return parent.complete(sourceManager.obtainSource(invocation.source()), invocation.arguments());
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    return parent.hasPermission(sourceManager.obtainSource(invocation.source()));
  }
}
