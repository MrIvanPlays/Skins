package com.mrivanplays.skins.core;

import com.mrivanplays.annotationconfig.core.Comment;
import com.mrivanplays.annotationconfig.core.ConfigObject;
import com.mrivanplays.annotationconfig.core.FieldTypeResolver;
import com.mrivanplays.annotationconfig.core.Key;
import com.mrivanplays.annotationconfig.core.Retrieve;
import com.mrivanplays.annotationconfig.core.TypeResolver;
import com.mrivanplays.skins.core.storage.StorageType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Comment("     Skins      #")
@Comment("by: MrIvanPlays #")
public final class SkinsConfiguration {

  @Comment("Check for updates")
  @Comment("Recommended leaving true")
  @Key("update-check")
  private boolean updateCheck = true;

  @ConfigObject private Messages messages = new Messages();

  @Key("storage-credentials")
  @ConfigObject
  private DatabaseCredentials storageCredentials = new DatabaseCredentials();

  @Comment("All configurable messages")
  public static final class Messages {

    @Key("no-permission")
    private String noPermission = "&cYou don't have permission to perform this command.";

    @Key("no-console")
    private String noConsole = "&cNo console.";

    @Key("command-usage")
    private String commandUsage = "&cUsage: /skinset <premium player name>";

    private String cooldown =
        "&cYou have to wait %timeLeft% more second(s) before using that command";

    @Key("not-premium")
    private String notPremium = "&cThis player isn't premium.";

    @Key("skin-set-successfully")
    private String skinSetSuccessfully =
        "&aSkin set successfully! If it didn't applied, try reconnecting to the server!";

    @Key("skin-already-set")
    private String skinAlreadySet = "&cThat skin is already set on you.";

    @Key("skin-menu-previous-page-label")
    private String skinMenuPreviousPageLabel = "&aPrevious page";

    @Key("skin-menu-next-page-label")
    private String skinMenuNextPageLabel = "&aNext page";

    @Key("skin-menu-close-page-label")
    private String skinMenuClosePageLabel = "Close inventory";

    @Key("skin-menu-head-name")
    private String skinMenuHeadName = "%name% 's skin";

    @Key("skin-menu-inventory")
    private String skinMenuInventory = "List of skins (Page #%pageNum%)";

    @Key("skin-menu-cannot-fetch-data")
    private String skinMenuCannotFetchData =
        "We're sorry, we weren't able to retrieve data about the owner of the skin. :( ";

    @Key("skin-menu-lore")
    private List<String> skinMenuLore =
        Arrays.asList(
            "Left click to set the skin",
            "(Keep in mind this skin is being cached and may not be up to date)");

    public String getNoPermission() {
      return noPermission;
    }

    public String getNoConsole() {
      return noConsole;
    }

    public String getCommandUsage() {
      return commandUsage;
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

  @Comment("Storage credentials")
  @Comment("Used to connect to database.")
  @Comment("Supported storage types: H2, SQLITE, MARIADB, MYSQL, POSTGRESQL, MONGODB")
  public static final class DatabaseCredentials {

    @Key("storage-type")
    @TypeResolver(StorageTypeResolver.class)
    private StorageType storageType = StorageType.H2;

    @Retrieve
    private String address = "localhost";

    @Retrieve
    private int port = 3306;

    @Retrieve
    private String database = "minecraft";

    @Retrieve
    private String username = "root";

    @Retrieve
    private String password = "1234";

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

    public static final class StorageTypeResolver implements FieldTypeResolver {

      @Override
      public Object toType(Object value, Field field) throws Exception {
        try {
          return StorageType.valueOf(String.valueOf(value).toUpperCase());
        } catch (IllegalArgumentException e) {
          return null;
        }
      }

      @Override
      public boolean shouldResolve(Class<?> fieldType) {
        return StorageType.class.isAssignableFrom(fieldType);
      }
    }
  }

  public boolean shouldUpdateCheck() {
    return updateCheck;
  }

  public Messages getMessages() {
    return messages;
  }

  public boolean isUpdateCheck() {
    return updateCheck;
  }

  public DatabaseCredentials getStorageCredentials() {
    return storageCredentials;
  }
}
