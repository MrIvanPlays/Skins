package com.mrivanplays.skins.core;

import com.mrivanplays.skins.core.storage.StorageType;
import java.util.Arrays;
import java.util.List;

public final class SkinsConfiguration {

  private boolean updateCheck = true;

  private final Messages messages;

  private final DatabaseCredentials storageCredentials;

  public SkinsConfiguration(ConfigurationAdapter adapter) {
    this.updateCheck = adapter.getBoolean("update-check", updateCheck);
    this.messages = new Messages(adapter);
    this.storageCredentials = new DatabaseCredentials(adapter);
  }

  public static final class Messages {

    private String noConsole = "&cNo console.";
    private String cooldown =
        "&cYou have to wait %timeLeft% more second(s) before using that command";
    private String notPremium = "&cThis player isn't premium.";
    private String skinSetSuccessfully =
        "&aSkin set successfully! If it didn't applied, try reconnecting to the server!";
    private String skinAlreadySet = "&cThat skin is already set on you.";
    private String skinMenuPreviousPageLabel = "&aPrevious page";
    private String skinMenuNextPageLabel = "&aNext page";
    private String skinMenuClosePageLabel = "Close inventory";
    private String skinMenuHeadName = "%name% 's skin";
    private String skinMenuInventory = "List of skins (Page #%pageNum%)";
    private String skinMenuCannotFetchData =
        "We're sorry, we weren't able to retrieve data about the owner of the skin. :( ";
    private List<String> skinMenuLore =
        Arrays.asList(
            "Left click to set the skin",
            "(Keep in mind this skin is being cached and may not be up to date)");

    public Messages(ConfigurationAdapter adapter) {
      this.noConsole = adapter.getString("messages.no-console", noConsole);
      this.cooldown = adapter.getString("messages.cooldown", cooldown);
      this.notPremium = adapter.getString("messages.not-premium", notPremium);
      this.skinSetSuccessfully =
          adapter.getString("messages.skin-set-successfully", skinSetSuccessfully);
      this.skinAlreadySet = adapter.getString("messages.skin-already-set", skinAlreadySet);
      this.skinMenuPreviousPageLabel =
          adapter.getString("messages.skin-menu-previous-page-label", skinMenuPreviousPageLabel);
      this.skinMenuNextPageLabel =
          adapter.getString("messages.skin-menu-next-page-label", skinMenuNextPageLabel);
      this.skinMenuClosePageLabel =
          adapter.getString("messages.skin-menu-close-page-label", skinMenuClosePageLabel);
      this.skinMenuHeadName = adapter.getString("messages.skin-menu-head-name", skinMenuHeadName);
      this.skinMenuInventory = adapter.getString("messages.skin-menu-inventory", skinMenuInventory);
      this.skinMenuCannotFetchData =
          adapter.getString("messages.skin-menu-cannot-fetch-data", skinMenuCannotFetchData);
      this.skinMenuLore = adapter.getStringList("messages.skin-menu-lore", skinMenuLore);
    }

    public String getNoConsole() {
      return noConsole;
    }

    public String getCooldown() {
      return cooldown;
    }

    public String getNotPremium() {
      return notPremium;
    }

    public String getSkinSetSuccessfully() {
      return skinSetSuccessfully;
    }

    public String getSkinAlreadySet() {
      return skinAlreadySet;
    }

    public String getSkinMenuPreviousPageLabel() {
      return skinMenuPreviousPageLabel;
    }

    public String getSkinMenuNextPageLabel() {
      return skinMenuNextPageLabel;
    }

    public String getSkinMenuClosePageLabel() {
      return skinMenuClosePageLabel;
    }

    public String getSkinMenuHeadName() {
      return skinMenuHeadName;
    }

    public String getSkinMenuInventory() {
      return skinMenuInventory;
    }

    public String getSkinMenuCannotFetchData() {
      return skinMenuCannotFetchData;
    }

    public List<String> getSkinMenuLore() {
      return skinMenuLore;
    }
  }

  public static final class DatabaseCredentials {

    private StorageType storageType = StorageType.H2;
    private String address = "localhost";
    private int port = 3306;
    private String database = "minecraft";
    private String username = "root";
    private String password = "1234";

    public DatabaseCredentials(ConfigurationAdapter adapter) {
      try {
        this.storageType =
            StorageType.valueOf(
                adapter.getString("storage-credentials.storage-type", storageType.name()));
      } catch (IllegalArgumentException ignored) {
      }
      this.address = adapter.getString("storage-credentials.address", address);
      this.port = adapter.getInt("storage-credentials.port", port);
      this.database = adapter.getString("storage-credentials.database", database);
      this.username = adapter.getString("storage-credentials.username", username);
      this.password = adapter.getString("storage-credentials.password", password);
    }

    public StorageType getStorageType() {
      return storageType;
    }

    public String getAddress() {
      return address;
    }

    public int getPort() {
      return port;
    }

    public String getDatabase() {
      return database;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }
  }

  public boolean shouldUpdateCheck() {
    return updateCheck;
  }

  public Messages getMessages() {
    return messages;
  }

  public DatabaseCredentials getStorageCredentials() {
    return storageCredentials;
  }
}
