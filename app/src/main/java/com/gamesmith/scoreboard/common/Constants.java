package com.gamesmith.scoreboard.common;

/**
 * Created by clocksmith on 5/24/15.
 */
public class Constants {
  public static final String FIREBASE_URL = "https://boiling-heat-4798.firebaseio.com/";
  public static final String FIREBASE_STATIC_URL = "https://scoreboard-static.firebaseio.com/";

  public static final int DEFAULT_ANIMATION_TIME_MILLIS = 350;

  // TODO(clocksmith): Delete MIN_ROOM_NUMBER and MAX_ROOM_NUMBER and make them a function of MIN_ROOM_NUMBER.
  public static final int NUM_ROOM_NUMBER_DIGITS = 6;
  public static final int MIN_ROOM_NUMBER = 100000;
  public static final int MAX_ROOM_NUMBER = 999999;

  public static final String ROOM_NUMBER = "playerName";
  public static final String PLAYER_ID = "playerId";

  public static final int MAX_HP = 12;
  public static final int STARTING_HP = 10;
  public static final int MAX_VP = 20;
  public static final int STARTING_VP = 0;

  public static final int NUM_COLUMNS_IN_USER_GRID = 2;

  public static final int PULSE_ANIMATION_DURATION_MILLIS = 375;
}
