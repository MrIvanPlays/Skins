package com.mrivanplays.skins.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class UserCooldown {

  private Cache<UUID, Long> cache;
  private final long durationMillis;

  public UserCooldown(int time) {
    this.durationMillis = TimeUnit.SECONDS.toMillis(time);
    this.cache = Caffeine.newBuilder().expireAfterWrite(time, TimeUnit.SECONDS).build();
  }

  public void cooldown(UUID uuid) {
    cache.put(uuid, System.currentTimeMillis() + durationMillis);
  }

  public long getTimeLeft(UUID uuid) {
    Long cooldownedAt = cache.getIfPresent(uuid);
    if (cooldownedAt == null) {
      return 0;
    }
    return (cooldownedAt - System.currentTimeMillis()) / 1000;
  }
}
