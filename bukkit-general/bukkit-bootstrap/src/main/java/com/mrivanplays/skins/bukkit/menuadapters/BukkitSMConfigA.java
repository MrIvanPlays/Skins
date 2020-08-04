package com.mrivanplays.skins.bukkit.menuadapters;

import com.mrivanplays.skins.bukkit_general.SkinsConfigAdapter;
import com.mrivanplays.skins.core.SkinsConfiguration;
import java.util.List;

public class BukkitSMConfigA implements SkinsConfigAdapter {

  private final SkinsConfiguration config;

  public BukkitSMConfigA(SkinsConfiguration config) {
    this.config = config;
  }

  @Override
  public String getSkinMenuPreviousPageLabel() {
    return config.getMessages().getSkinMenuPreviousPageLabel();
  }

  @Override
  public String getSkinMenuNextPageLabel() {
    return config.getMessages().getSkinMenuNextPageLabel();
  }

  @Override
  public String getSkinMenuClosePageLabel() {
    return config.getMessages().getSkinMenuClosePageLabel();
  }

  @Override
  public String getSkinMenuHeadName() {
    return config.getMessages().getSkinMenuHeadName();
  }

  @Override
  public List<String> getSkinMenuLore() {
    return config.getMessages().getSkinMenuLore();
  }

  @Override
  public String getSkinMenuInventory() {
    return config.getMessages().getSkinMenuInventory();
  }

  @Override
  public String getSkinMenuCannotFetchData() {
    return config.getMessages().getSkinMenuCannotFetchData();
  }
}
