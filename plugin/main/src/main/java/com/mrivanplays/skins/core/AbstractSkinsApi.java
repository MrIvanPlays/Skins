package com.mrivanplays.skins.core;

import com.google.common.base.Preconditions;
import com.mrivanplays.skins.api.DataProvider;
import com.mrivanplays.skins.api.MojangResponse;
import com.mrivanplays.skins.api.Skin;
import com.mrivanplays.skins.api.SkinsApi;
import com.mrivanplays.skins.api.SkinsVersionInfo;
import com.mrivanplays.skins.api.SkullItemBuilder;
import com.mrivanplays.skins.core.SkullItemBuilderImpl.SkullItemBuilderData;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSkinsApi implements SkinsApi {

  private final SkinFetcher skinFetcher;
  private final SkinStorage skinStorage;
  private final Function<SkullItemBuilderData, ItemStack> itemBuilderTransformer;
  private final Function<ItemStack, MojangResponse> skullOwnerTransformer;
  private final SkinsVersionInfo versionInfo;

  public AbstractSkinsApi(InitializationData initializationData) {
    skinStorage = new SkinStorage(initializationData.getDataFolder());
    skinFetcher = new SkinFetcher(skinStorage, initializationData.getDataProvider());
    this.itemBuilderTransformer = initializationData.getItemBuilderTransformer();
    this.skullOwnerTransformer = initializationData.getSkullOwnerTransformer();
    this.versionInfo = initializationData.getVersionInfo();
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

  public MojangResponse getSkin(String username, UUID uuid) {
    return skinFetcher.getSkin(username, uuid);
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
    return new SkullItemBuilderImpl(itemBuilderTransformer);
  }

  @Override
  public void setDataProvider(@NotNull DataProvider dataProvider) {
    Preconditions.checkNotNull(dataProvider, "dataProvider");
    if (skinFetcher.getDataProvider().equals(dataProvider)) {
      return;
    }
    skinFetcher.setDataProvider(dataProvider);
  }

  @Override
  @NotNull
  public SkinsVersionInfo getVersionInfo() {
    return versionInfo;
  }

  @Override
  @Nullable
  public MojangResponse getSkullOwner(@NotNull ItemStack item) {
    Objects.requireNonNull(item, "item");
    return skullOwnerTransformer.apply(item);
  }

  public void modifyStoredSkin(UUID uuid, Skin skin) {
    Optional<StoredSkin> storedSkinOptional = skinStorage.getStoredSkin(skin.getOwner());
    if (storedSkinOptional.isPresent()) {
      StoredSkin storedSkin = storedSkinOptional.get();
      Optional<StoredSkin> currentStoredSkinOptional = skinStorage.getPlayerSetSkin(uuid);
      if (currentStoredSkinOptional.isPresent()) {
        StoredSkin currentStoredSkin = currentStoredSkinOptional.get();
        if (currentStoredSkin.equals(storedSkin)) {
          return;
        }
        StoredSkin dup = currentStoredSkin.duplicate();
        dup.removeAcquirer(uuid);
        skinStorage.updateAcquirers(dup);
      }
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

  protected abstract void setNPCSkin(Player player, Skin skin);

  public SkinStorage getSkinStorage() {
    return skinStorage;
  }
}
