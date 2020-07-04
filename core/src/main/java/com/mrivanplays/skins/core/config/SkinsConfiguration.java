package com.mrivanplays.skins.core.config;

import com.mrivanplays.annotationconfig.core.Comment;
import com.mrivanplays.annotationconfig.core.ConfigObject;
import com.mrivanplays.annotationconfig.core.Key;
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

  public boolean shouldUpdateCheck() {
    return updateCheck;
  }

  public Messages getMessages() {
    return messages;
  }
}
