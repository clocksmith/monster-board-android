package com.gamesmith.monsterboard.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gamesmith.monsterboard.common.Constants;

/**
 * Created by clocksmith on 1/29/16.
 */
public class PreferencesUtils {
  private static final String TAG = PreferencesUtils.class.getSimpleName();

  private static SharedPreferences getPreferences(Context context) {
    return context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
  }

  public static void setRoomNumber(Context context, int roomNumber) {
    getPreferences(context).edit().putInt(Constants.ROOM_NUMBER, roomNumber).commit();
  }

  public static int getRoomNumber(Context context) {
    int roomNumber = getPreferences(context).getInt(Constants.ROOM_NUMBER, -1);
    return roomNumber;
  }

  public static void setPlayerId(Context context, int playerId) {
    Log.d(TAG, "setPlayerId: " + playerId);
    getPreferences(context).edit().putInt(Constants.PLAYER_ID, playerId).commit();
  }

  public static int getPlayerId(Context context) {
    int playerId = getPreferences(context).getInt(Constants.PLAYER_ID, -1);
    return playerId;
  }

  public static void setPlayerName(Context context, String playerName) {
    Log.d(TAG, "setPlayerName: " + playerName);
    getPreferences(context).edit().putString(Constants.PLAYER_NAME, playerName).commit();
  }

  public static String getPlayerName(Context context) {
    String playerName = getPreferences(context).getString(Constants.PLAYER_NAME, "");
    return playerName;
  }

  public static void setMonsterName(Context context, String monsterName) {
    Log.d(TAG, "setMonsterName: " + monsterName);
    getPreferences(context).edit().putString(Constants.MONSTER_NAME, monsterName).commit();
  }

  public static String getMonsterName(Context context) {
    String monsterName = getPreferences(context).getString(Constants.MONSTER_NAME, "");
    return monsterName;
  }
}
