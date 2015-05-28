package com.gamesmith.scoreboard.firebase;

import com.firebase.client.Firebase;

/**
 * Created by clocksmith on 5/27/15.
 */
public class FirebaseUtils {
  public static Firebase getRooms(Firebase root) {
    return root.child("rooms");
  }

  public static Firebase getRoom(Firebase root, int roomNumber) {
    return getRooms(root).child(String.valueOf(roomNumber));
  }

  public static Firebase getPlayer(Firebase root, int roomNumber, int playerId) {
    return getRoom(root, roomNumber).child(String.valueOf(playerId));
  }
}
