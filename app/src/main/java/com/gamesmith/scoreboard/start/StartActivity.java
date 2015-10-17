package com.gamesmith.scoreboard.start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.gamesmith.scoreboard.common.Monster;
import com.gamesmith.scoreboard.common.NameProvider;
import com.gamesmith.scoreboard.common.firebase.FirebaseUtils;
import com.gamesmith.scoreboard.common.firebase.Player;
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
  private List<String> mDefaultNames;
  private Random mRandom;
  private int mJoiningPlayerId;

  private EditText mPlayerInput;

  ProgressDialog mProgressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);

    BusProvider.getInstance().register(this);
    Firebase.setAndroidContext(this);
    mFirebase = new Firebase(Constants.FIREBASE_URL);
    mRandom = new Random();

    mPlayerInput = (EditText) findViewById(R.id.activity_start_playerInput);

    mPlayerInput.setFilters(new InputFilter[] { new InputFilter.AllCaps() });

    mDefaultNames = NameProvider.getNames(this);
    setRandomName();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    BusProvider.getInstance().unregister(this);
  }

  private void createRoom() {
    mProgressDialog = ProgressDialog.show(this, "Creating room", "hold tight...");
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
        if (mProgressDialog != null) {
          mProgressDialog.dismiss();
        }
        if (committed) {
          startRoomActivity(roomNumber, 0);
        }
      }
    });
  }

  private void joinRoom(int roomNumber) {
    if (!this.isFinishing()) {
      mProgressDialog = ProgressDialog.show(this, "Joining room", "hold tight...");
    }

    FirebaseUtils.getRoom(mFirebase, roomNumber).runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData currentData) {
        if (currentData != null) {
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
        if (mProgressDialog != null) {
          mProgressDialog.dismiss();
        }
        if (committed) {
          startRoomActivity(Integer.parseInt(currentData.getKey()), mJoiningPlayerId);
        } else {
          // TODO(clocksmith)
          Log.w(TAG, "not committed");
        }
      }
    });
  }

  public void handleRoomTaken() {
    // TODO(clocksmith): Do something in this extremely rare case.
    Log.w(TAG, "handleRoomTaken");
  }

  public void handleInvalidRoom() {
    // TODO(clocksmith)
    Log.w(TAG, "handleInvalidRoom");
  }

  private int getRandomRoomNumber() {
    return mRandom.nextInt(Constants.MAX_ROOM_NUMBER - Constants.MIN_ROOM_NUMBER + 1) + Constants.MIN_ROOM_NUMBER;
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
    Log.d(TAG, "startRoomActivity()");
    Intent intent = new Intent(StartActivity.this, RoomActivity.class);
    intent.putExtra(Constants.ROOM_NUMBER, roomNumber);
    intent.putExtra(Constants.PLAYER_ID, playerId);
    startActivity(intent);
    finish();
  }

  private void setRandomName() {
    if (mPlayerInput.getText() == null || mPlayerInput.getText().toString().isEmpty()) {
      mPlayerInput.setText(mDefaultNames.get(mRandom.nextInt(mDefaultNames.size())));
    }
  }

  @Subscribe
  public void on(CreateOrJoinView.CreateButtonClickedEvent event) {
    createRoom();
  }

  @Subscribe
  public void on(RoomNumberInput.RoomNumberInputFinishedEvent event) {
    Log.d(TAG, "on(RoomNumberInput.RoomNumberInputFinishedEvent()");
    joinRoom(event.roomNumber);
  }
}
