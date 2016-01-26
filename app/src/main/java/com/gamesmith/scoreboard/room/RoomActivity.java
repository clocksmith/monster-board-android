package com.gamesmith.scoreboard.room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.common.BusProvider;
import com.gamesmith.scoreboard.common.firebase.FirebaseUtils;
import com.gamesmith.scoreboard.common.firebase.Player;
import com.gamesmith.scoreboard.common.Constants;
import com.gamesmith.scoreboard.start.StartActivity;
import com.google.common.collect.Lists;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

/**
 * Created by clocksmith on 5/24/15.
 */
public class RoomActivity extends AppCompatActivity {
  private static final String TAG = RoomActivity.class.getSimpleName();

  private Firebase mFirebase;
  private RoomValueEventListener mRoomValueEventListener;
  private int mRoomNumber;
  private int mPlayerId;

  private MainPlayerCard mMainPlayerCard;
  private RecyclerView mRecyclerView;
  private UserRecyclerViewAdapter mAdapter;

  private boolean mReceyclerViewLayoutCalled;
  private boolean mOnResumeExecuted;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room);

    BusProvider.getInstance().register(this);
    mFirebase = new Firebase(Constants.FIREBASE_URL);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mRoomNumber = bundle.getInt(Constants.ROOM_NUMBER);
      mPlayerId = bundle.getInt(Constants.PLAYER_ID);
    }

    mMainPlayerCard = (MainPlayerCard) findViewById(R.id.activity_room_mainUserCard);
    mRecyclerView = (RecyclerView) findViewById(R.id.activity_room_recyclerView);

    mAdapter = new UserRecyclerViewAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, Constants.NUM_COLUMNS_IN_USER_GRID));
    mRecyclerView.addItemDecoration(
        new BorderedItemDecoration(this.getResources().getDimensionPixelOffset(R.dimen.default_padding)));
    mRecyclerView.setHasFixedSize(false);
    mRecyclerView.setItemAnimator(new ScaleInAnimator());

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowTitleEnabled(false);
      getSupportActionBar().setDisplayShowCustomEnabled(true);
      getSupportActionBar().setCustomView(new RoomActionBar(this, mRoomNumber));
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
    }

    mRoomValueEventListener = new RoomValueEventListener();

    if (!mReceyclerViewLayoutCalled) {
      mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          int height = mRecyclerView.getHeight();
          mAdapter.setItemHeight(height / 3);
          FirebaseUtils.getRoom(mFirebase, mRoomNumber).addValueEventListener(mRoomValueEventListener);
          mReceyclerViewLayoutCalled = true;
        }
      });
    }

    FirebaseUtils.getPlayer(mFirebase, mRoomNumber, mPlayerId).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        String name = FirebaseUtils.getChildValueDeNull(dataSnapshot, "name", "");
        String monster = FirebaseUtils.getChildValueDeNull(dataSnapshot, "monster", "");
        int hp = FirebaseUtils.getChildValueDeNull(dataSnapshot, "hp", 0L).intValue();
        int vp = FirebaseUtils.getChildValueDeNull(dataSnapshot, "vp", 0L).intValue();

        mMainPlayerCard.setName(name);
        mMainPlayerCard.setMonster(monster);
        mMainPlayerCard.setHp(hp);
        mMainPlayerCard.setVp(vp);
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
        // TODO(clocksmith)
        Log.w(TAG, "onCancelled()");
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume()");

    if (mReceyclerViewLayoutCalled && mOnResumeExecuted) {
      FirebaseUtils.getRoom(mFirebase, mRoomNumber).removeEventListener(mRoomValueEventListener);
      FirebaseUtils.getRoom(mFirebase, mRoomNumber).addValueEventListener(mRoomValueEventListener);
    }

    mOnResumeExecuted = true;
  }

  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause()");
    FirebaseUtils.getRoom(mFirebase, mRoomNumber).removeEventListener(mRoomValueEventListener);
    moveTaskToBack(true);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy()");
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        confirmLeaveRoom();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    confirmLeaveRoom();
  }

  private void confirmLeaveRoom() {
    new AlertDialog.Builder(this)
        .setMessage(R.string.leave_room)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            leaveRoom();
          }
        })
        .setNegativeButton(R.string.no, null)
        .show();
  }

  private void leaveRoom() {
    FirebaseUtils.getPlayer(mFirebase, mRoomNumber, mPlayerId).removeValue();
    Intent intent = new Intent(RoomActivity.this, StartActivity.class);
    startActivity(intent);
    finish();
  }

  private void update(DataSnapshot snapshot) {
    List<Player> players = Lists.newArrayList();
    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
      @SuppressWarnings("unchecked")
      Map<String, Object> user = (Map<String, Object>) userSnapshot.getValue();

      Player player = new Player();
      player.name = (String) user.get("name");
      player.monster = (String) user.get("monster");
      player.hp = ((Long) user.get("hp")).intValue();
      player.vp = ((Long) user.get("vp")).intValue();
      players.add(player);
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

  private class RoomValueEventListener implements ValueEventListener {
    @Override
    public void onDataChange(DataSnapshot snapshot) {
      update(snapshot);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
      // TODO(clocksmith)
      Log.w(TAG, "onCancelled()");
    }
  }
}
