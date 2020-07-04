package com.mrivanplays.skins.core.scheduler;

import java.util.concurrent.Executor;

public interface Scheduler {

  Executor sync();

  Executor async();
}
