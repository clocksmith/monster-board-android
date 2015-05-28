package com.gamesmith.scoreboard.room;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.common.BusProvider;
import com.gamesmith.scoreboard.firebase.FirebaseUtils;
import com.gamesmith.scoreboard.firebase.Player;
import com.gamesmith.scoreboard.common.Constants;
import com.google.common.collect.Lists;
import com.squareup.otto.Subscribe;

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

  private MainPlayerCard mMainPlayerCard;
  private RecyclerView mRecyclerView;
  private UserRecyclerViewAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room);

    Firebase.setAndroidContext(this);
    mFirebase = new Firebase(Constants.FIREBASE_URL);

    BusProvider.getInstance().register(this);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mRoomNumber = bundle.getInt(Constants.ROOM_NUMBER);
      mRoomNumberString = String.valueOf(mRoomNumber);
      mPlayerId = bundle.getInt(Constants.PLAYER_ID);
    }

    mMainPlayerCard = (MainPlayerCard) findViewById(R.id.activity_game_mainUserCard);
    mRecyclerView = (RecyclerView) findViewById(R.id.activity_game_recyclerView);

    mAdapter = new UserRecyclerViewAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, Constants.NUM_COLUMNS_IN_USER_GRID));
    mRecyclerView.addItemDecoration(
        new BorderedItemDecoration(this.getResources().getDimensionPixelOffset(R.dimen.default_padding)));
    mRecyclerView.setHasFixedSize(false);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowCustomEnabled(true);
      getSupportActionBar().setCustomView(new RoomActionBar(this, mRoomNumber));
      getSupportActionBar().setTitle("");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    FirebaseUtils.getRoom(mFirebase, mRoomNumber).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        Log.d(TAG, "mFirebaseRooms onDataChange()");
        update(snapshot);
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
        // TODO(clocksmith)
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void update(DataSnapshot snapshot) {
    List<Player> players = Lists.newArrayList();
    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
      String name = (String) userSnapshot.child("name").getValue();
      String monster = (String) userSnapshot.child("monster").getValue();
      int hp = ((Long) userSnapshot.child("hp").getValue()).intValue();
      int vp = ((Long) userSnapshot.child("vp").getValue()).intValue();

      Player player = new Player();
      player.name = name;
      player.monster = monster;
      player.hp = hp;
      player.vp = vp;
      players.add(player);

      if (userSnapshot.getKey().equals(String.valueOf(mPlayerId))) {
        mMainPlayerCard.setName(name);
        mMainPlayerCard.setMonster(monster);
        mMainPlayerCard.setHp(hp);
        mMainPlayerCard.setVp(vp);
      }
    }
    mAdapter.update(players);
  }

  @Subscribe
  public void on(MainPlayerCard.MainMonsterChangedEvent event) {
    FirebaseUtils.getPlayer(mFirebase, mRoomNumber, mPlayerId).child("monster").setValue(event.monster.getName());
  }

  @Subscribe
  public void on(MainPlayerCard.MainHpChangedEvent event) {
    FirebaseUtils.getPlayer(mFirebase, mRoomNumber, mPlayerId).child("hp").setValue(event.hp);
  }

  @Subscribe
  public void on(MainPlayerCard.MainVpChangedEvent event) {
    FirebaseUtils.getPlayer(mFirebase, mRoomNumber, mPlayerId).child("vp").setValue(event.vp);
  }

  public class BorderedItemDecoration extends RecyclerView.ItemDecoration {
    private int mWidth;

    public BorderedItemDecoration(int width) {
      mWidth = width;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      int childPosition = parent.getChildPosition(view);
      int numColumns = Constants.NUM_COLUMNS_IN_USER_GRID;
      outRect.left = childPosition % numColumns== 0 ? mWidth : mWidth / 2;
      outRect.right = childPosition % numColumns == numColumns - 1 ? mWidth : mWidth / 2;
      outRect.bottom = mWidth;
      outRect.top = 0;
    }
  }
}
