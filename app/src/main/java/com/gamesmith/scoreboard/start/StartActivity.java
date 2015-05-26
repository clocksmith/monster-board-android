package com.gamesmith.scoreboard.start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.gamesmith.scoreboard.firebase.User;
import com.gamesmith.scoreboard.common.BusProvider;
import com.gamesmith.scoreboard.common.Constants;
import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.room.RoomActivity;
import com.google.common.collect.Lists;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Random;


public class StartActivity extends Activity {
  private static final String TAG = StartActivity.class.getSimpleName();

  private Firebase mFirebase;
  private Firebase mFirebaseStatic;
  private List<String> mDefaultPlayers;
  private List<String> mMonsters;
  private Random mRandom;

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
        mMonsters = (List<String>) dataSnapshot.child("monstersNewYork").getValue();
        if (mPlayerInput.getText() == null || mPlayerInput.getText().toString().isEmpty()) {
          mPlayerInput.setText(mDefaultPlayers.get(mRandom.nextInt(mDefaultPlayers.size())));
        }
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {

      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_start, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void attemptToCreateRoom() {
    final ProgressDialog progressDialog = ProgressDialog.show(this, "Creating room", "hold tight...");

    final int pin = mRandom.nextInt(Constants.MAX_PIN - Constants.MIN_PIN + 1) + Constants.MIN_PIN;
    mFirebase.child("rooms").runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData currentData) {
        String pinString = String.valueOf(pin);
        if (currentData.hasChild(pinString) && !((List) currentData.child(pinString).getValue()).isEmpty()) {
          handleRoomTaken();
          Transaction.abort();
        } else {
          User user = new User();
          user.name = mPlayerInput.getText().toString();
          user.monster = mMonsters.get(mRandom.nextInt(mMonsters.size()));
          user.hp = Constants.STARTING_HP;
          user.vp = Constants.STARTING_VP;
          List<User> users = Lists.newArrayList();
          users.add(user);
          currentData.child(pinString).setValue(users);
        }
        return Transaction.success(currentData);
      }

      @Override
      public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
        progressDialog.dismiss();
        Log.d(TAG, "currentData" + currentData.toString());
        Intent intent = new Intent(StartActivity.this, RoomActivity.class);
        intent.putExtra(Constants.PLAYER_ID, 0);
        intent.putExtra(Constants.ROOM_NUMBER, pin);
        startActivity(intent);
      }
    });
  }

  public void handleRoomTaken() {
    // TODO(clocksmith): Do something in this extremely rare case.
  }

  @Subscribe
  public void on(CreateOrJoinView.CreateButtonClickedEvent event) {
    attemptToCreateRoom();
  }
}
