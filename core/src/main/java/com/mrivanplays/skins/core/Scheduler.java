package com.mrivanplays.skins.core;

import java.util.concurrent.Executor;

public interface Scheduler {

  Executor sync();

  Executor async();
}
