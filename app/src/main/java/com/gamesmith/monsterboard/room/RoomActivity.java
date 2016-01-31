package com.gamesmith.monsterboard.room;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.common.providers.BusProvider;
import com.gamesmith.monsterboard.common.utils.PreferencesUtils;
import com.gamesmith.monsterboard.common.utils.FirebaseUtils;
import com.gamesmith.monsterboard.common.model.Player;
import com.gamesmith.monsterboard.common.Constants;
import com.google.common.collect.Lists;
import com.squareup.otto.Subscribe;

import java.util.List;

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

/**
 * Created by clocksmith on 5/24/15.
 */
public class RoomActivity extends AppCompatActivity {
  private static final String TAG = RoomActivity.class.getSimpleName();

  private static final int NUM_COLUMNS_IN_USER_GRID = 2;

  private RoomValueEventListener mRoomValueEventListener;
  private int mRoomNumber;
  private int mPlayerId;

  private MainPlayerCard mMainPlayerCard;
  private RecyclerView mRecyclerView;
  private PlayerRecyclerViewAdapter mAdapter;

  private boolean mReceyclerViewLayoutCalled;
  private boolean mOnResumeExecuted;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room);

    BusProvider.getInstance().register(this);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mRoomNumber = bundle.getInt(Constants.ROOM_NUMBER);
      mPlayerId = bundle.getInt(Constants.PLAYER_ID);
    }

    mMainPlayerCard = (MainPlayerCard) findViewById(R.id.activity_room_mainUserCard);
    mRecyclerView = (RecyclerView) findViewById(R.id.activity_room_recyclerView);

    mAdapter = new PlayerRecyclerViewAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUM_COLUMNS_IN_USER_GRID));
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
          FirebaseUtils.getRoom(mRoomNumber).removeEventListener(mRoomValueEventListener);
          FirebaseUtils.getRoom(mRoomNumber).addValueEventListener(mRoomValueEventListener);
          mReceyclerViewLayoutCalled = true;
        }
      });
    }

    FirebaseUtils.getPlayer(mRoomNumber, mPlayerId).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        mMainPlayerCard.setPlayer(dataSnapshot.getValue(new GenericTypeIndicator<Player>() {}));
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
        Log.w(TAG, "onCancelled()");
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();

    if (mReceyclerViewLayoutCalled && mOnResumeExecuted) {
      FirebaseUtils.getRoom(mRoomNumber).removeEventListener(mRoomValueEventListener);
      FirebaseUtils.getRoom(mRoomNumber).addValueEventListener(mRoomValueEventListener);
    }

    mOnResumeExecuted = true;
  }

  public void onPause() {
    super.onPause();
    FirebaseUtils.getRoom(mRoomNumber).removeEventListener(mRoomValueEventListener);
    moveTaskToBack(true);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    BusProvider.getInstance().unregister(this);
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
    FirebaseUtils.getPlayer( mRoomNumber, mPlayerId).removeValue();
    PreferencesUtils.setRoomNumber(this, -1);
    PreferencesUtils.setPlayerId(this, -1);
    finish();
  }

  private void update(DataSnapshot snapshot) {
    List<Player> players = Lists.newArrayList();
    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
      players.add(userSnapshot.getValue(new GenericTypeIndicator<Player>() {}));
    }
    mAdapter.update(players);
  }

  @Subscribe
  public void on(MainPlayerCard.MainMonsterChangedEvent event) {
    PreferencesUtils.setMonsterName(this, event.monster.getName());
    FirebaseUtils.getPlayer(mRoomNumber, mPlayerId).child("monster").setValue(event.monster.getName());
  }

  @Subscribe
  public void on(MainPlayerCard.MainHpChangedEvent event) {
    FirebaseUtils.getPlayer(mRoomNumber, mPlayerId).child("hp").setValue(event.hp);
  }

  @Subscribe
  public void on(MainPlayerCard.MainVpChangedEvent event) {
    FirebaseUtils.getPlayer(mRoomNumber, mPlayerId).child("vp").setValue(event.vp);
  }

  private class RoomValueEventListener implements ValueEventListener {
    @Override
    public void onDataChange(DataSnapshot snapshot) {
      update(snapshot);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
      Log.w(TAG, "onCancelled()");
    }
  }

  private class BorderedItemDecoration extends RecyclerView.ItemDecoration {
    private int mWidth;

    public BorderedItemDecoration(int width) {
      mWidth = width;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      int childPosition = parent.getChildPosition(view);
      int numColumns = NUM_COLUMNS_IN_USER_GRID;
      outRect.left = childPosition % numColumns== 0 ? mWidth : mWidth / 2;
      outRect.right = childPosition % numColumns == numColumns - 1 ? mWidth : mWidth / 2;
      outRect.bottom = mWidth;
      outRect.top = 0;
    }
  }
}
