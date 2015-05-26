package com.gamesmith.scoreboard.common;

import com.squareup.otto.Bus;

/**
 * Created by clocksmith on 5/24/15.
 */
public final class BusProvider {
  private static final Bus BUS = new Bus();

  public static Bus getInstance() {
    return BUS;
  }

  private BusProvider() {
    // No instances.
  }
}