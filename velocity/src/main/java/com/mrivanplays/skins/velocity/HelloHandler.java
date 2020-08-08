package com.mrivanplays.skins.velocity;

import java.util.ArrayList;
import java.util.List;

public class HelloHandler {

  private static List<String> HELLO_RECEIVED = new ArrayList<>();

  public static void addHelloReceived(String server) {
    HELLO_RECEIVED.add(server);
  }

  public static boolean hasReceivedHello(String server) {
    return HELLO_RECEIVED.contains(server);
  }
}
