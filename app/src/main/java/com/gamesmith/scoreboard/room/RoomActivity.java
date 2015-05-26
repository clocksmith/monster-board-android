package com.gamesmith.scoreboard.room;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.firebase.User;
import com.gamesmith.scoreboard.common.Constants;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by clocksmith on 5/24/15.
 */
public class RoomActivity extends AppCompatActivity {
  private static final String TAG = RoomActivity.class.getSimpleName();

  private Firebase mFirebase;
  private int mRoomNumber;
  private String mRoomNumberString;
  private int mPlayerId;

  private MainUserCard mMainUserCard;
  private RecyclerView mRecyclerView;
  private RoomRecyclerViewAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room);

    Firebase.setAndroidContext(this);
    mFirebase = new Firebase(Constants.FIREBASE_URL);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mRoomNumber = bundle.getInt(Constants.ROOM_NUMBER);
      mRoomNumberString = String.valueOf(mRoomNumber);
      mPlayerId = bundle.getInt(Constants.PLAYER_ID);
    }

    mMainUserCard = (MainUserCard) findViewById(R.id.activity_game_mainUserCard);
    mRecyclerView = (RecyclerView) findViewById(R.id.activity_game_recyclerView);

    mAdapter = new RoomRecyclerViewAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setHasFixedSize(false);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("Room number: " + mRoomNumberString);
    }


    mFirebase.child("rooms").child(mRoomNumberString).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        update(snapshot);
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });
  }

  private void update(DataSnapshot snapshot) {
    List<User> users = Lists.newArrayList();
    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
      String name = (String) userSnapshot.child("name").getValue();
      String monster = (String) userSnapshot.child("monster").getValue();
      Log.d(TAG, ("snapshot: " + snapshot.toString()));
      Log.d(TAG, ("snapshot.getKey(): " + snapshot.getKey()));
      Log.d(TAG, ("mPlayerId: " + mPlayerId));
      if (userSnapshot.getKey().equals(String.valueOf(mPlayerId))) {
        mMainUserCard.setName(name);
        mMainUserCard.setMonster(monster);
      }
      User user = new User();
      user.name = name;
      user.monster = monster;
      users.add(user);
    }
    mAdapter.update(users);
  }
}
