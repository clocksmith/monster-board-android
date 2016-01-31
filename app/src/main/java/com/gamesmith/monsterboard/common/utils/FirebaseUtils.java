package com.gamesmith.monsterboard.common.utils;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

/**
 * Created by clocksmith on 5/27/15.
 */
public class FirebaseUtils {
  private static final String FIREBASE_URL = "https://boiling-heat-4798.firebaseio.com/";

  public static Firebase getRoot() {
    return new Firebase(FIREBASE_URL);
  }


  public static Firebase getRooms() {
    return getRoot().child("rooms");
  }

  public static Firebase getRoom(int roomNumber) {
    return getRooms().child(String.valueOf(roomNumber));
  }

  public static Firebase getPlayer(int roomNumber, int playerId) {
    return getRoom(roomNumber).child(String.valueOf(playerId));
  }

  public static <T extends Object> T getChildValueDeNull(DataSnapshot dataSnapshot, String childPath, T defaultValue) {
    DataSnapshot child = dataSnapshot.child(childPath);
    if (child == null || child.getValue() == null) {
      return defaultValue;
    } else {
      return (T) child.getValue();
    }
  }
}
