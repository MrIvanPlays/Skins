package com.mrivanplays.skins.core;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface Scheduler {

  Executor sync();

  Executor async();

  final class Default implements Scheduler {

    private final ExecutorService sync;
    private final ExecutorService async;

    public Default() {
      this.sync = Executors.newSingleThreadExecutor();
      this.async = Executors.newScheduledThreadPool(6);
    }

    @Override
    public Executor sync() {
      return sync;
    }

    @Override
    public Executor async() {
      return async;
    }
  }
}
