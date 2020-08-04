package com.mrivanplays.skins.bukkit_general;

import java.util.List;

public interface SkinsConfigAdapter {

  String getSkinMenuPreviousPageLabel();

  String getSkinMenuNextPageLabel();

  String getSkinMenuClosePageLabel();

  String getSkinMenuHeadName();

  List<String> getSkinMenuLore();

  String getSkinMenuInventory();

  String getSkinMenuCannotFetchData();
}
