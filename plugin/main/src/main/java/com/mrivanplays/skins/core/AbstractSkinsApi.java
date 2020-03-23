package com.mrivanplays.skins.core;

import com.google.common.base.Preconditions;
import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.api.SkullItemBuilder;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSkinsApi implements SkinsApi {

  private final SkinFetcher skinFetcher;
  private final SkinStorage skinStorage;
  private final Function<SkullItemBuilderData, ItemStack> transformer;

  public AbstractSkinsApi(InitializationData initializationData) {
    skinStorage = new SkinStorage(initializationData.getDataFolder());
    skinFetcher = new SkinFetcher(skinStorage, initializationData.getDataProvider());
    this.transformer = initializationData.getTransformer();
  }

  @Override
  @Deprecated
  public Optional<Skin> getSetSkin(@NotNull Player player) {
    Preconditions.checkNotNull(player, "player");
    return skinStorage.getPlayerSetSkin(player.getUniqueId()).map(StoredSkin::getSkin);
  }

  @Override
  public @NotNull MojangResponse getSetSkinResponse(@NotNull Player player) {
    Preconditions.checkNotNull(player, "player");
    return skinStorage
        .getPlayerSetSkin(player.getUniqueId())
        .map(storedSkin -> getSkin(storedSkin.getName()))
        .orElseGet(() -> getOriginalSkinResponse(player));
  }

  public MojangResponse getSetSkinResponse(String playerName, UUID playerUUID) {
    return skinStorage
        .getPlayerSetSkin(playerUUID)
        .map(storedSkin -> getSkin(storedSkin.getName()))
        .orElseGet(() -> getSkin(playerName));
  }

  @Override
  @NotNull
  public MojangResponse getSkin(@NotNull String username) {
    Preconditions.checkNotNull(username, "username");
    return skinFetcher.getSkin(username);
  }

  @Override
  public boolean setSkin(@NotNull Player player, @NotNull MojangResponse skin) {
    Preconditions.checkNotNull(player, "player");
    Preconditions.checkNotNull(skin, "skin");
    if (skin.getSkin().isPresent()) {
      Skin skinObj = skin.getSkin().get();
      modifyStoredSkin(player.getUniqueId(), skinObj);
      setNPCSkin(player, skinObj);
      return true;
    } else {
      return false;
    }
  }

  @Override
  @NotNull
  public SkullItemBuilder newSkullItemBuilder() {
    return new SkullItemBuilderImpl(transformer);
  }

  @Override
  public void setDataProvider(@NotNull DataProvider dataProvider) {
    Preconditions.checkNotNull(dataProvider, "dataProvider");
    if (skinFetcher.getDataProvider().equals(dataProvider)) {
      return;
    }
    skinFetcher.setDataProvider(dataProvider);
  }

  public void modifyStoredSkin(UUID uuid, Skin skin) {
    Optional<StoredSkin> storedSkinOptional = skinStorage.getStoredSkin(skin.getOwner());
    if (storedSkinOptional.isPresent()) {
      StoredSkin storedSkin = storedSkinOptional.get();
      if (storedSkin.getAcquirers().contains(uuid.toString())) {
        return;
      }
      StoredSkin dup = storedSkin.duplicate();
      dup.addAcquirer(uuid);
      skinStorage.updateAcquirers(dup);
    }
    // do nothing when not present
    // stored skin isn't present when skin wasn't present when data was fetched.
  }

  protected void setNPCSkin(Player player, Skin skin) {}

  public SkinStorage getSkinStorage() {
    return skinStorage;
  }
}
