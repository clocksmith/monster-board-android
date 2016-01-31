package com.gamesmith.monsterboard.common;

/**
 * Created by clocksmith on 5/24/15.
 */
public class Constants {
  // Animation constants.
  public static final int DEFAULT_ANIMATION_TIME_MILLIS = 350;
  public static final int PULSE_ANIMATION_DURATION_MILLIS = 375;

  // Room number constants
  public static final int NUM_ROOM_NUMBER_DIGITS = 6;
  public static final int MIN_ROOM_NUMBER = (int) Math.pow(10, NUM_ROOM_NUMBER_DIGITS - 1);
  public static final int MAX_ROOM_NUMBER = MIN_ROOM_NUMBER * 10 - 1;

  // Nvp keys
  public static final String ROOM_NUMBER = "roomNumber";
  public static final String PLAYER_ID = "playerId";
  public static final String PLAYER_NAME = "playerName";
  public static final String MONSTER_NAME = "monsterName";

  // Game constants
  public static final Long MAX_HP = 12L;
  public static final Long STARTING_HP = 10L;
  public static final Long MAX_VP = 20L;
  public static final Long STARTING_VP = 0L;
}
