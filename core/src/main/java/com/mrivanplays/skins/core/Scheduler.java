package com.mrivanplays.skins.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface Scheduler {

  Executor sync();

  Executor async();

  final class Default implements Scheduler {

    private final Executor sync;
    private final ExecutorService async;

    public Default(Executor sync) {
      this.sync = sync;
      this.async =
          Executors.newCachedThreadPool(
              new ThreadFactoryBuilder().setNameFormat("skins-async-#%1$d").build());
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
