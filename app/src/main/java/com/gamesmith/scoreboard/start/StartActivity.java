package com.gamesmith.scoreboard.start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.gamesmith.scoreboard.common.Monster;
import com.gamesmith.scoreboard.firebase.FirebaseUtils;
import com.gamesmith.scoreboard.firebase.Player;
import com.gamesmith.scoreboard.common.BusProvider;
import com.gamesmith.scoreboard.common.Constants;
import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.room.RoomActivity;
import com.google.common.collect.ImmutableList;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Random;


public class StartActivity extends Activity {
  private static final String TAG = StartActivity.class.getSimpleName();

  private Firebase mFirebase;
  private Firebase mFirebaseStatic;
  private List<String> mDefaultPlayers;
  private Random mRandom;
  private int mJoiningPlayerId;

  private EditText mPlayerInput;
  private CreateOrJoinView mCreateOrJoinView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);

    BusProvider.getInstance().register(this);
    Firebase.setAndroidContext(this);
    mFirebase = new Firebase(Constants.FIREBASE_URL);
    mFirebaseStatic = new Firebase(Constants.FIREBASE_STATIC_URL);
    mRandom = new Random();

    mPlayerInput = (EditText) findViewById(R.id.activity_start_playerInput);
    mCreateOrJoinView = (CreateOrJoinView) findViewById(R.id.activity_start_createOrJoinView);

    mFirebaseStatic.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        mDefaultPlayers = (List<String>) dataSnapshot.child("playerNames").getValue();
        if (mPlayerInput.getText() == null || mPlayerInput.getText().toString().isEmpty()) {
          mPlayerInput.setText(mDefaultPlayers.get(mRandom.nextInt(mDefaultPlayers.size())));
        }
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
        // TODO(clocksmith)
      }
    });
  }

  private void createRoom() {
    final ProgressDialog progressDialog = ProgressDialog.show(this, "Creating room", "hold tight...");
    final int roomNumber = getRandomRoomNumber();

    FirebaseUtils.getRooms(mFirebase).runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData currentData) {
        String roomNumberString = String.valueOf(roomNumber);
        if (currentData.hasChild(roomNumberString)
            && !((List) currentData.child(roomNumberString).getValue()).isEmpty()) {
          handleRoomTaken();
          return Transaction.abort();
        } else {
          List<Player> players = ImmutableList.of(getNewUser());
          currentData.child(roomNumberString).setValue(players);
          return Transaction.success(currentData);
        }
      }

      @Override
      public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
        progressDialog.dismiss();
        startRoomActivity(roomNumber, 0);
      }
    });
  }

  private void joinRoom(final int roomNumber) {
    final ProgressDialog progressDialog = ProgressDialog.show(this, "Joining room", "hold tight...");

    FirebaseUtils.getRooms(mFirebase).child(String.valueOf(roomNumber)).runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData currentData) {
        if (currentData != null && currentData.hasChildren()) {
          Player newPlayer = getNewUser();

          int highestPlayerId = -1;
          for (MutableData playerData : currentData.getChildren()) {
            int playerId = Integer.parseInt(playerData.getKey());
            if (playerId > highestPlayerId) {
              highestPlayerId = playerId;
            }
          }
          mJoiningPlayerId = highestPlayerId + 1;

          currentData.child(String.valueOf(mJoiningPlayerId)).setValue(newPlayer);
          return Transaction.success(currentData);
        } else {
          handleInvalidRoom();
          return Transaction.abort();
        }
      }

      @Override
      public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
        progressDialog.dismiss();
        startRoomActivity(roomNumber, mJoiningPlayerId);
      }
    });
  }

  public void handleRoomTaken() {
    // TODO(clocksmith): Do something in this extremely rare case.
  }

  public void handleInvalidRoom() {
    // TODO(clocksmith)
  }

  private int getRandomRoomNumber() {
    return mRandom.nextInt(Constants.MAX_PIN - Constants.MIN_PIN + 1) + Constants.MIN_PIN;
  }

  private Player getNewUser() {
    Player newPlayer = new Player();
    newPlayer.name = mPlayerInput.getText().toString();
    newPlayer.monster = Monster.values()[mRandom.nextInt(Monster.values().length)].getName();
    newPlayer.hp = Constants.STARTING_HP;
    newPlayer.vp = Constants.STARTING_VP;
    return newPlayer;
  }

  private void startRoomActivity(int roomNumber, int playerId) {
    Intent intent = new Intent(StartActivity.this, RoomActivity.class);
    intent.putExtra(Constants.PLAYER_ID, playerId);
    intent.putExtra(Constants.ROOM_NUMBER, roomNumber);
    startActivity(intent);
  }

  @Subscribe
  public void on(CreateOrJoinView.CreateButtonClickedEvent event) {
    createRoom();
  }

  @Subscribe
  public void on(CreateOrJoinView.RoomNumberInputFinishedEvent event) {
    joinRoom(event.roomNumber);
  }
}
