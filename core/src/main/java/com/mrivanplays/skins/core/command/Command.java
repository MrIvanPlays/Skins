package com.mrivanplays.skins.core.command;

import java.util.List;

public interface Command {

  void execute(CommandSource source, String[] args);

  List<String> complete(CommandSource source, String[] args);
}
